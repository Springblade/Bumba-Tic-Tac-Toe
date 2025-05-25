package com.bumba.tic_tac_toe;

import java.io.IOException;

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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LobbyController {

    @FXML private ListView<GameEntry> gameListView;
    @FXML private Button createGameButton;
    @FXML private Button quickJoinButton;
    @FXML private Button spectateButton;
    @FXML private Button refreshButton;
    @FXML private Button joinGameButton;
    @FXML private Button new3x3game;
    @FXML private Button new9x9game;
    @FXML private Label IDLabel;
    @FXML private Label statusLabel;

    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private Stage stage;

    private ClientMain client;

    public static class GameEntry {
        String ID, status;
        GameEntry(String ID, String status) { this.ID = ID; this.status = status; }
    }

    @FXML
    public void initialize() {
        ObservableList<GameEntry> games = FXCollections.observableArrayList();
        if(client != null && client.getGameList() != null) {
            for (String game : client.getGameList()) {
                String[] parts = game.split(",");
                GameEntry newGame = new GameEntry(parts[0], parts[1]);
                games.add(newGame);
            }
        }

        new3x3game.setOnAction(event -> createGame(3));
        new9x9game.setOnAction(event -> createGame(9));


        gameListView.setItems(games);
        gameListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(GameEntry newGame, boolean empty) {
                super.updateItem(newGame, empty);
                if (empty || newGame == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("server_item.fxml"));
                        HBox hbox = loader.load();

                        IDLabel.setText(newGame.ID);
                        statusLabel.setText(newGame.status);

                        joinGameButton.setOnAction(event -> System.out.println("Connecting to " + newGame.ID));
                        quickJoinButton.setOnAction(event -> System.out.println("Connecting to " + newGame.ID));
                        spectateButton.setOnAction(event -> System.out.println("Showing info for " + newGame.ID));


                        setGraphic(hbox);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void refresh() {
        gameListView.getItems().clear();
        ObservableList<GameEntry> games = FXCollections.observableArrayList();
        if(client != null && client.getGameList() != null) {
            for (String game : client.getGameList()) {
                String[] parts = game.split(",");
                GameEntry newGame = new GameEntry(parts[0], parts[1]);
                games.add(newGame);
            }
        }
        gameListView.setItems(games);
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
            ClientMain.getClient().sendMessage("create_game-" + ClientMain.getClient().getUsername());
            System.out.println("Creating " + dimension + "x" + dimension + " game...");
            
            // Hide the dimension selection buttons after creating
            new3x3game.setVisible(false);
            new9x9game.setVisible(false);
            createGameButton.setVisible(true);
            
            // Refresh the game list to show the new game
            refresh();
        }
    }

    protected void joinGame() {
        // Get selected game from list
        GameEntry selectedGame = gameListView.getSelectionModel().getSelectedItem();
        if(selectedGame != null) {
            joinGame(selectedGame.ID);
        } else {
            System.out.println("No game selected to join");
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

    protected void quickJoin() {
        // Send quick join message to server
        // Format: "quick_join"
        if(ClientMain.getClient() != null) {
            ClientMain.getClient().sendMessage("quick_join");
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
            root = FXMLLoader.load(getClass().getResource("/com/bumba/tic_tac_toe/game.fxml"));
            scene = new Scene(root);
            stage = (Stage) createGameButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
