package com.bumba.tic_tac_toe;

import java.io.IOException;
import java.util.List;

import com.bumba.tic_tac_toe.LobbyController.GameEntry;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LobbyController {

    @FXML private ListView<GameEntry> gameListView;
    @FXML private ListView<RankEntry> rankingsListView;
    @FXML private Button createGameButton;
    @FXML private Button quickJoinButton;
    @FXML private Button refreshButton;
    @FXML private Button new3x3game;
    @FXML private Button new9x9game;
    @FXML private TabPane tabPane;
    @FXML private Tab gameTab;
    @FXML private Tab rankings;

    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private Stage stage;

    private ClientMain client;
    private int count = 0;

    private List<String> gameList;
    private List<String> rankList;

    public static class GameEntry {
        String ID, creator, status, dimension;
        GameEntry(String ID, String creator, String status, String dimension) { this.ID = ID; this.creator = creator; this.status = status; this.dimension = dimension; }
    }

    public static class RankEntry {
        int rank;
        String username, elo;
        RankEntry(int rank, String username, String elo) { this.rank=rank; this.username = username; this.elo = elo; }
    }

    @FXML
    public void initialize() {
        new3x3game.setOnAction(event -> createGame(3));
        new9x9game.setOnAction(event -> createGame(9));
        ClientMain.setLobbyController(this);

        // Initialize game list
        ObservableList<GameEntry> games = FXCollections.observableArrayList();
        gameListView.setItems(games);

        // Initialize rankings list
        ObservableList<RankEntry> ranks = FXCollections.observableArrayList();
        rankingsListView.setItems(ranks);

        // Request initial data from the server
        refreshFromServer();
        requestRankings();

        // Set up game list cell factory
        gameListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(GameEntry gameEntry, boolean empty) {
                super.updateItem(gameEntry, empty);
                if (empty || gameEntry == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    Label idLabel = new Label(gameEntry.ID);
                    idLabel.setPrefWidth(70);

                    Label creatorLabel = new Label(gameEntry.creator);
                    creatorLabel.setPrefWidth(80);

                    Label statusLabel = new Label(gameEntry.status);
                    statusLabel.setPrefWidth(80);

                    Label typeLabel = new Label(gameEntry.dimension.equals("9") ? "9x9" : "3x3");
                    typeLabel.setPrefWidth(20);

                    Button joinButton = new Button("Join");
                    joinButton.setOnAction(event -> joinGame(gameEntry.ID));

                    Button spectateButton = new Button("Spectate");
                    spectateButton.setOnAction(event -> spectate(gameEntry.ID));

                    hbox.getChildren().addAll(idLabel, creatorLabel, statusLabel, typeLabel, joinButton, spectateButton);
                    setGraphic(hbox);
                }
            }
        });

        // Set up rankings list cell factory
        rankingsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(RankEntry rankEntry, boolean empty) {
                super.updateItem(rankEntry, empty);
                if (empty || rankEntry == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    Label rankLabel = new Label(String.valueOf(rankEntry.rank));
                    rankLabel.setPrefWidth(70);

                    Label nameLabel = new Label(rankEntry.username);
                    nameLabel.setPrefWidth(80);

                    Label eloLabel = new Label(rankEntry.elo);
                    eloLabel.setPrefWidth(80);

                    hbox.getChildren().addAll(rankLabel, nameLabel, eloLabel);
                    setGraphic(hbox);
                }
            }
        });
    }

    public void refresh() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if(selectedTab == gameTab) {
            refreshFromServer();
        } else if (selectedTab == rankings) {
            requestRankings();
        }
    }

    // Method to update game list from server response
    public void updateGameListFromServer(String gameListContent) {
        Platform.runLater(() -> {
            gameListView.getItems().clear();
            
            if (!gameListContent.isEmpty()) {
                // Parse: "gameId1:creator1:status1-gameId2:creator2:status2"
                String[] games = gameListContent.split("-");
                
                for (String gameInfo : games) {
                    if (!gameInfo.trim().isEmpty()) {
                        String[] parts = gameInfo.split(":");
                        if (parts.length >= 4) {
                            String gameId = parts[0];
                            String creator = parts[1];
                            String status = parts[2];
                            String dimension = parts[3];

                            GameEntry newGame = new GameEntry(gameId, creator, status, dimension);
                            gameListView.getItems().add(newGame);
                        }
                    }
                }
            }
            
            System.out.println("Updated lobby with " + gameListView.getItems().size() + " games");
        });
    }

    // Method to update rankings from server response
    public void updateRankingsFromServer(String rankingsContent) {
        Platform.runLater(() -> {
            if (rankingsListView != null) {
                rankingsListView.getItems().clear();

                if (!rankingsContent.isEmpty()) {
                    // Parse: "username1_elo1-username2_elo2-username3_elo3"
                    String[] rankingsArray = rankingsContent.split("-");

                    for (int i = 0; i < rankingsArray.length; i++) {
                        String rankingInfo = rankingsArray[i];
                        if (!rankingInfo.trim().isEmpty()) {
                            String[] parts = rankingInfo.split("_");
                            if (parts.length >= 2) {
                                String username = parts[0];
                                String elo = parts[1];
                                int rank = i + 1;

                                RankEntry newRanking = new RankEntry(rank, username, elo);
                                rankingsListView.getItems().add(newRanking);
                            }
                        }
                    }
                }

                System.out.println("Updated rankings with " + rankingsListView.getItems().size() + " entries");
            }
        });
    }

    // Add single game to list (for real-time updates)
    public void addGameToLobby(String gameInfo) {
        Platform.runLater(() -> {
            // Parse: "gameId:username:status"
            String[] parts = gameInfo.split(":");
            if (parts.length >= 3) {
                String gameId = parts[0];
                String creator = parts[1];
                String status = parts[2];
                String dimension = parts[3];
                
                GameEntry newGame = new GameEntry(gameId, creator, status, dimension);
                
                // Check if game already exists
                boolean exists = gameListView.getItems().stream()
                        .anyMatch(game -> game.ID.equals(gameId));
                
                if (!exists) {
                    gameListView.getItems().add(newGame);
                    System.out.println("Added game to lobby: " + gameId);
                }
            }
        });
    }

    // Remove game from list
    public void removeGameFromLobby(String gameId) {
        Platform.runLater(() -> {
            gameListView.getItems().removeIf(game -> game.ID.equals(gameId));
            System.out.println("Removed game from lobby: " + gameId);
        });
    }

    // Clear lists
    public void clearGameList() {
        Platform.runLater(() -> {
            gameListView.getItems().clear();
            System.out.println("Cleared lobby game list");
        });
    }

    public void clearRankings() {
        Platform.runLater(() -> {
            if (rankingsListView != null) {
                rankingsListView.getItems().clear();
                System.out.println("Cleared rankings list");
            }
        });
    }

    // Method to refresh from server
    @FXML
    public void refreshFromServer() {
        if(ClientMain.getClient() != null) {
            ClientMain.getClient().sendMessage("list_games");
        }
    }

    // Method to request rankings
    public void requestRankings() {
        if(ClientMain.getClient() != null) {
            ClientMain.requestRankings();
        }
    }

    @FXML
    protected void chooseBoard(ActionEvent event) {
        createGameButton.setVisible(false);
        new3x3game.setVisible(true);
        new9x9game.setVisible(true);
    }

    @FXML
    protected void createGame(int dimension) {
        // Send create game message to server
        // Format: "create_game-username" (dimension would be set when game starts)
        if(ClientMain.getClient() != null) {
            ClientMain.setCurrentGameInfo("", dimension);
            ClientMain.getClient().sendMessage("create_game-" + ClientMain.getClient().getUsername() + "-" + dimension);
            System.out.println("Creating " + dimension + "x" + dimension + " game...");
            
            // Hide the dimension selection buttons after creating
            new3x3game.setVisible(false);
            new9x9game.setVisible(false);
            createGameButton.setVisible(true);
            
            // Refresh the game list to show the new game
            refresh();
        }
    }

    @FXML
    protected void joinGame() {
        // Get selected game from list
        GameEntry selectedGame = gameListView.getSelectionModel().getSelectedItem();
        if(selectedGame != null) {
            joinGame(selectedGame.ID);
        } else {
            System.out.println("Error joining game");
        }
    }
    
    // Overloaded method for direct game ID joining
    protected void joinGame(String gameId) {
        // Send join game message to server
        // Format: "join_game-gameId"
        if(ClientMain.getClient() != null && gameId != null) {
            ClientMain.getClient().sendMessage("join_game-" + gameId);
            System.out.println("Joining game: " + gameId);
            
            // Transition to game scene after joining
            transitionToGame();
        }
    }

    @FXML
    protected void spectate() {
        // Get selected game from list
        GameEntry selectedGame = gameListView.getSelectionModel().getSelectedItem();
        if(selectedGame != null) {
            spectate(selectedGame.ID);
        } else {
            System.out.println("No game selected to spectate");
        }
    }

    // Overloaded method for direct game ID spectating
    protected void spectate(String gameId) {
        // Send spectate message to server
        // Format: "spectate-gameId"
        if(ClientMain.getClient() != null && gameId != null) {
            ClientMain.getClient().sendMessage("spectate-" + gameId);
            System.out.println("Spectating game: " + gameId);
            
            // Transition to game scene as spectator
            transitionToGame();
        }
    }

    @FXML
    protected void quickJoin() {
        // Send quick join message to server
        // Format: "quick_join"
        if(ClientMain.getClient() != null) {
            // client.getClient().sendMessage("quick_join");
            ClientMain.getClient().sendMessage("quick_join-" + ClientMain.getClient().getUsername());
            System.out.println("Quick joining available game...");
            
            // The server will either:
            // 1. Put player in waiting state for new game
            // 2. Join existing waiting game and start immediately
            // Handle transition based on server response in ClientMain
        }
    }


    @FXML
    public void transitionToGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bumba/tic_tac_toe/game.fxml"));
            root = loader.load();
            
            // Get the game controller and register it
            GameController gameController = loader.getController();
            ClientMain.setGameController(gameController);
            
        Scene scene = new Scene(root);
        
        // Get the stage from any available control instead of using the field
        Stage currentStage = null;
        
        // Try multiple controls to get the stage
        if (createGameButton != null && createGameButton.getScene() != null) {
            currentStage = (Stage) createGameButton.getScene().getWindow();
        } else if (gameListView != null && gameListView.getScene() != null) {
            currentStage = (Stage) gameListView.getScene().getWindow();
        } else if (refreshButton != null && refreshButton.getScene() != null) {
            currentStage = (Stage) refreshButton.getScene().getWindow();
        } else if (tabPane != null && tabPane.getScene() != null) {
            currentStage = (Stage) tabPane.getScene().getWindow();
        }
        
        if (currentStage != null) {
            currentStage.setScene(scene);
            currentStage.setTitle("Tic Tac Toe - Game");
            currentStage.show();
            System.out.println("Transitioned to game scene");
        } else {
            System.err.println("Could not find stage to transition to game scene");
        }
        } catch (IOException e) {
            System.err.println("Failed to transition to game scene: " + e.getMessage());
            e.printStackTrace();
        }
    }
}