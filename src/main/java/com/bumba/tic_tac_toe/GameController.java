package com.bumba.tic_tac_toe;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class GameController {
    // Game state variables
    private int dimension = 3;
    private int turn = 1;
    private boolean gameEnded = false;
    private boolean isMyTurn = false;
    private String gameResult = "";
    private List<ImageView> grids;

    // UI components
    @FXML private GridPane board3x3;
    @FXML private Label statusLabel;
    @FXML private Label timerLabel;
    @FXML private Label playername1;
    @FXML private Label playername2;
    @FXML private Label elo1;
    @FXML private Label elo2;
    @FXML private Button ffButton;
    @FXML private ListView<String> chatArea;
    @FXML private TextField chatBox;
    @FXML private Button sendButton;

    // Game resources
    private Image symbolX;
    private Image symbolO;
    private MediaPlayer clickSfxPlayer;

    @FXML
    public void initialize() {
        try {
            System.out.println("GameController initializing...");
            
            // Register this controller with ClientMain
            ClientMain.setGameController(this);
            
            // Initialize UI components
            initializeChat();
            initializeTimer();
            
            // Get dimension from ClientMain
            this.dimension = ClientMain.getCurrentGameDimension();
            if (dimension <= 0) {
                System.out.println("Warning: Invalid dimension, defaulting to 3x3");
                dimension = 3;
            }
            
            // Initialize player info
            initializePlayerInfo();
            
            // Load resources
            loadGameResources();
            
            // Initialize game board
            initializeGameBoard();
            
            // Set up forfeit button
            if (ffButton != null) {
                ffButton.setOnAction(event -> handleForfeit());
            }
            
            System.out.println("GameController initialization complete");
        } catch (Exception e) {
            System.err.println("Error in GameController initialization: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Initialization Error", "Failed to initialize game: " + e.getMessage());
        }
    }

    private void loadGameResources() {
        try {
            // Load X and O symbols
            symbolX = new Image(getClass().getResourceAsStream("/com/bumba/tic_tac_toe/images/x.png"));
            symbolO = new Image(getClass().getResourceAsStream("/com/bumba/tic_tac_toe/images/o.png"));
            
            if (symbolX == null || symbolX.isError()) {
                System.err.println("Failed to load X symbol image");
            }
            
            if (symbolO == null || symbolO.isError()) {
                System.err.println("Failed to load O symbol image");
            }
            
            // Load click sound
            try {
                URL resource = getClass().getResource("/com/bumba/tic_tac_toe/sounds/click.mp3");
                if (resource != null) {
                    Media clickSound = new Media(resource.toString());
                    clickSfxPlayer = new MediaPlayer(clickSound);
                } else {
                    System.err.println("Click sound resource not found");
                }
            } catch (Exception e) {
                System.err.println("Error loading click sound: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Error loading resources: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeChat() {
        if (chatArea != null) {
            chatArea.getItems().clear();
            chatArea.getItems().add("Game chat started. Be respectful!");
        }
        
        if (sendButton != null) {
            sendButton.setOnAction(event -> sendChatMessage());
        }
    }

    private void initializeTimer() {
        // Initialize timer if needed
    }

    private void initializePlayerInfo() {
        try {
            // Get player info from ClientMain
            String player1 = ClientMain.getCurrentPlayer1();
            String player2 = ClientMain.getCurrentPlayer2();
            int player1Elo = ClientMain.getCurrentPlayer1Elo();
            int player2Elo = ClientMain.getCurrentPlayer2Elo();
            
            // Update UI
            if (playername1 != null) playername1.setText(player1);
            if (playername2 != null) playername2.setText(player2);
            if (elo1 != null) elo1.setText("ELO: " + player1Elo);
            if (elo2 != null) elo2.setText("ELO: " + player2Elo);
            
        } catch (Exception e) {
            System.err.println("Error initializing player info: " + e.getMessage());
        }
    }
        
    private void initializeGameBoard() {
        try {
            // Check if board exists
            if (board3x3 == null) {
                System.err.println("ERROR: board3x3 is null!");
                return;
            }
            
            // Initialize grids list if it's null
            if (grids == null) {
                grids = new ArrayList<>();
            } else {
                grids.clear(); // Clear existing entries
            }
            
            // Get all grid cells from the FXML
            for (Node node : board3x3.getChildren()) {
                if (node instanceof ImageView) {
                    ImageView grid = (ImageView) node;
                    grids.add(grid);
                    
                    // Clear any existing images
                    grid.setImage(null);
                    
                    // Add click handler
                    final int position = grids.size() - 1;
                    grid.setOnMouseClicked(event -> {
                        if (!gameEnded && isMyTurn) {
                            handleCellClick(position);
                        }
                    });
                }
            }
            
            // Debug output
            System.out.println("Initialized " + grids.size() + " grid cells");
            
            // Load symbols if not already loaded
            if (symbolX == null || symbolO == null) {
                loadGameResources();
            }
            
            // Reset game state
            resetBoard();
            
        } catch (Exception e) {
            System.err.println("Error initializing game board: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resetBoard() {
        // Clear all cells
        for (ImageView grid : grids) {
            grid.setImage(null);
        }
        
        // Reset game state
        turn = 1;
        gameEnded = false;
        
        // Determine if it's our turn
        String currentUsername = ClientMain.getClient() != null ? ClientMain.getClient().getUsername() : "";
        String player1 = ClientMain.getCurrentPlayer1();
        isMyTurn = currentUsername.equals(player1);
        
        // Update UI
        updateTurnIndicator();
    }

    private void updateTurnIndicator() {
        if (statusLabel != null) {
            if (gameEnded) {
                statusLabel.setText(gameResult);
                statusLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
            } else {
                statusLabel.setText(isMyTurn ? "Your turn" : "Opponent's turn");
                statusLabel.setStyle("-fx-text-fill: " + (isMyTurn ? "green" : "red") + "; -fx-font-weight: bold;");
            }
        }
        
        // Enable/disable board based on turn
        if (board3x3 != null) {
            board3x3.setDisable(!isMyTurn || gameEnded);
        }
    }

    // Method to update dimension when received from server
    public void setGameDimension(int dimension) {
        this.dimension = dimension;
        ClientMain.setCurrentGameInfo(ClientMain.getCurrentGameId(), dimension);
        Platform.runLater(() -> initializeGameBoard());
    }
    
    @FXML
    protected void onClick() {
        clickSfxPlayer.seek(clickSfxPlayer.getStartTime());
        clickSfxPlayer.play();
    }

    public void handleGameMove(String moveData) {
        try {
            // Parse move data: gameId-position-player
            String[] parts = moveData.split("-");
            if (parts.length < 3) {
                System.err.println("Invalid move data: " + moveData);
                return;
            }
            
            String gameId = parts[0];
            int position = Integer.parseInt(parts[1]);
            String player = parts[2];
            
            System.out.println("Received move: Game=" + gameId + ", Pos=" + position + ", Player=" + player);
            
            // Check if this is for our current game
            if (!gameId.equals(ClientMain.getCurrentGameId())) {
                System.out.println("Move is for a different game, ignoring");
                return;
            }
            
            // Update the board
            Platform.runLater(() -> {
                try {
                    // Check if position is valid
                    if (position < 0 || position >= grids.size()) {
                        System.err.println("Invalid position: " + position);
                        return;
                    }
                    
                    // Get the correct symbol
                    String currentUsername = ClientMain.getClient().getUsername();
                    String player1 = ClientMain.getCurrentPlayer1();
                    String player2 = ClientMain.getCurrentPlayer2();
                    
                    boolean isPlayer1Move = player.equals(player1);
                    Image symbol = isPlayer1Move ? symbolX : symbolO;
                    
                    // Update the UI
                    grids.get(position).setImage(symbol);
                    
                    // Play sound if available
                    if (clickSfxPlayer != null) {
                        clickSfxPlayer.stop();
                        clickSfxPlayer.play();
                    }
                    
                    // Update turn
                    turn++;
                    isMyTurn = player.equals(currentUsername) ? false : true;
                    updateTurnIndicator();
                    
                } catch (Exception e) {
                    System.err.println("Error handling move: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error processing move: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCellClick(int position) {
        try {
            // Check if cell is already occupied
            if (grids.get(position).getImage() != null) {
                System.out.println("Cell already occupied");
                return;
            }
            
            // Get current player info
            String currentUsername = ClientMain.getClient().getUsername();
            String gameId = ClientMain.getCurrentGameId();
            
            // Show symbol immediately (will be confirmed by server later)
            String player1 = ClientMain.getCurrentPlayer1();
            boolean isPlayer1 = currentUsername.equals(player1);
            Image symbolToUse = isPlayer1 ? symbolX : symbolO;
            grids.get(position).setImage(symbolToUse);
            
            // Play sound if available
            if (clickSfxPlayer != null) {
                clickSfxPlayer.stop();
                clickSfxPlayer.play();
            }
            
            // Send move to server
            ClientMain.sendGameMove(String.valueOf(position));
            
            // Temporarily disable board until server confirms move
            isMyTurn = false;
            updateTurnIndicator();
            
            System.out.println("Sent move: position " + position);
            
        } catch (Exception e) {
            System.err.println("Error sending move: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Move Error", "Failed to send move: " + e.getMessage());
        }
    }

    public void handleOpponentMove(int position, String playerWhoMoved) {
        System.out.println("Handling opponent move: " + playerWhoMoved + " at position " + position);
        
        // Get current user info
        String currentUsername = ClientMain.getClient() != null ? ClientMain.getClient().getUsername() : "";
        String player1 = ClientMain.getCurrentPlayer1();
        String player2 = ClientMain.getCurrentPlayer2();
        
        // Determine which symbol to place based on who moved
        Image symbolToPlace;
        if (playerWhoMoved.equals(player1)) {
            // Player 1 always uses X
            symbolToPlace = symbolX;
        } else {
            // Player 2 always uses O
            symbolToPlace = symbolO;
        }
        
        if (position >= 0 && position < grids.size() && grids.get(position) != null) {
            grids.get(position).setImage(symbolToPlace);
            System.out.println("Placed " + (symbolToPlace == symbolX ? "X" : "O") + " at position " + position);
        }

        // Update turn - it's now our turn since opponent just moved
        isMyTurn = true;
        updateTurnIndicator();
        
        // Play sound if available
        if (clickSfxPlayer != null) {
            clickSfxPlayer.stop();
            clickSfxPlayer.play();
        }
    }
    
    public void handleGameEnd(String endReason, String winner) {
        gameEnded = true;
        
        // Get current username
        String currentUsername = ClientMain.getClient() != null ? ClientMain.getClient().getUsername() : "";
        
        // Set game result message
        if (endReason.equals("WIN")) {
            if (winner.equals(currentUsername)) {
                gameResult = "You won!";
            } else {
                gameResult = "You lost!";
            }
        } else if (endReason.equals("TIE")) {
            gameResult = "Game tied!";
        } else if (endReason.equals("FORFEIT")) {
            if (winner.equals(currentUsername)) {
                gameResult = "Opponent forfeited. You win!";
            } else {
                gameResult = "You forfeited. You lose!";
            }
        }
        
        // Update UI
        updateTurnIndicator();
        
        // Show game end dialog
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            
            if (endReason.equals("WIN")) {
                if (winner.equals(currentUsername)) {
                    alert.setHeaderText("You Won!");
                    alert.setContentText("Congratulations! You have won the game.");
                } else {
                    alert.setHeaderText("You Lost");
                    alert.setContentText("Better luck next time!");
                }
            } else if (endReason.equals("TIE")) {
                alert.setHeaderText("Game Tied");
                alert.setContentText("The game ended in a tie.");
            } else if (endReason.equals("FORFEIT")) {
                if (!winner.equals(currentUsername)) {
                    alert.setHeaderText("Opponent Forfeited");
                    alert.setContentText("Your opponent has left the game. You win!");
                } else {
                    alert.setHeaderText("You Forfeited");
                    alert.setContentText("You have left the game.");
                }
            }
            
            // Add a button to return to lobby
            ButtonType returnToLobbyButton = new ButtonType("Return to Lobby");
            alert.getButtonTypes().setAll(returnToLobbyButton);
            
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == returnToLobbyButton) {
                    backToLobbyScene();
                }
            });
        });
    }
    
    private void backToLobbyScene() {
        try {
            // Load lobby scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bumba/tic_tac_toe/lobby.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            
            // Get controller
            LobbyController lobbyController = loader.getController();
            ClientMain.setLobbyController(lobbyController);
            
            // Show lobby
            Stage stage = (Stage) board3x3.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tic Tac Toe - Lobby");
        } catch (Exception e) {
            System.err.println("Error loading lobby: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void addChatMessage(String message) {
        if (chatArea != null) {
            chatArea.getItems().add(message);
            chatArea.scrollTo(chatArea.getItems().size() - 1);
        }
    }
    
    @FXML
    private void sendChatMessage() {
        String message = chatBox.getText().trim();
        if (!message.isEmpty()) {
            ClientMain.sendChatMessage(message);
            chatBox.clear();
        }
    }

    @FXML
    private void handleChatKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendChatMessage();
        }
    }
    
    private void handleForfeit() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Forfeit Game");
            alert.setHeaderText("Are you sure you want to forfeit?");
            alert.setContentText("This will count as a loss.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String username = ClientMain.getClient().getUsername();
                String gameId = ClientMain.getCurrentGameId();
                
                // Send forfeit command
                ClientMain.getClient().sendMessage("forfeit-" + gameId + "-" + username);
            }
        } catch (Exception e) {
            System.err.println("Error handling forfeit: " + e.getMessage());
        }
    }

    public void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    public Scene getScene() {
        return board3x3.getScene();
    }
}
