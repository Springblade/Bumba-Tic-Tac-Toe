package com.bumba.tic_tac_toe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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

    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private Stage stage;

    private ClientMain client;
    private int dimension;
    private String symbol;
    ArrayList<ImageView> grids;
    private Image symbolX;
    private Image symbolO;
    private int turn;
    private Timeline gameTimer;
    private int secondsElapsed = 0;

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
            // Load X and O symbols with robust error handling
            symbolX = loadImageResource("/com/bumba/tic_tac_toe/img/X.png", "X");
            symbolO = loadImageResource("/com/bumba/tic_tac_toe/img/O.png", "O");
            
            // Load click sound
            try {
                URL soundResource = getClass().getResource("/com/bumba/tic_tac_toe/sfx/click.mp3");
                if (soundResource != null) {
                    Media clickSound = new Media(soundResource.toExternalForm());
                    clickSfxPlayer = new MediaPlayer(clickSound);
                    clickSfxPlayer.setVolume(0.5);
                } else {
                    System.err.println("Click sound not found");
                }
            } catch (Exception e) {
                System.err.println("Error loading sound: " + e.getMessage());
                // Continue without sound
            }
        } catch (Exception e) {
            System.err.println("Error loading resources: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Image loadImageResource(String path, String symbol) {
        try {
            // Try primary path
            URL resource = getClass().getResource(path);
            if (resource != null) {
                return new Image(resource.toExternalForm());
            }
            
            // Try alternative paths
            String[] altPaths = {
                "/img/" + symbol + ".png",
                "/" + symbol + ".png",
                "/images/" + symbol + ".png"
            };
            
            for (String altPath : altPaths) {
                resource = getClass().getResource(altPath);
                if (resource != null) {
                    return new Image(resource.toExternalForm());
                }
            }
            
            // Create fallback
            return createFallbackSymbol(symbol);
        } catch (Exception e) {
            System.err.println("Error loading " + symbol + " image: " + e.getMessage());
            return createFallbackSymbol(symbol);
        }
    }

    // Create a fallback symbol image when resources can't be loaded
    private Image createFallbackSymbol(String symbol) {
        try {
            // Create a simple canvas to draw the symbol
            Canvas canvas = new Canvas(100, 100);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            
            // Clear with white background
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, 100, 100);
            
            // Draw the symbol
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(8);
            
            if (symbol.equals("X")) {
                // Draw X
                gc.strokeLine(20, 20, 80, 80);
                gc.strokeLine(80, 20, 20, 80);
            } else {
                // Draw O
                gc.strokeOval(20, 20, 60, 60);
            }
            
            // Convert to image
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            return canvas.snapshot(params, null);
        } catch (Exception e) {
            System.err.println("Error creating fallback symbol: " + e.getMessage());
            return null;
        }
    }

    private void initializeChat() {
        try {
            if (chatArea != null) {
                chatArea.getItems().clear();
                chatArea.getItems().add("Game chat started");
            }
            
            if (sendButton != null) {
                sendButton.setOnAction(event -> sendChatMessage());
            }
        } catch (Exception e) {
            System.err.println("Error initializing chat: " + e.getMessage());
        }
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
                showErrorAlert("UI Error", "Game board not found in the UI.");
                return;
            }
            
            // Initialize grids list
            grids = new ArrayList<>();
            
            // Add all grid ImageViews to the list
            ImageView[] gridViews = {grid0, grid1, grid2, grid3, grid4, grid5, grid6, grid7, grid8};
            for (ImageView grid : gridViews) {
                if (grid != null) {
                    grids.add(grid);
                }
            }
            
            if (grids.size() != 9) {
                System.err.println("WARNING: Expected 9 grid cells, found " + grids.size());
            }
            
            // Clear any existing images and add click handlers
            for (int i = 0; i < grids.size(); i++) {
                final int position = i;
                ImageView grid = grids.get(i);
                
                // Clear image
                grid.setImage(null);
                
                // Add click handler
                grid.setOnMouseClicked(event -> {
                    if (!gameEnded && isMyTurn) {
                        handleCellClick(position);
                    }
                });
            }
            
            // Set initial game state
            resetBoard();
            
        } catch (Exception e) {
            System.err.println("Error initializing game board: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Initialization Error", "Failed to initialize game board: " + e.getMessage());
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
        board3x3.setDisable(!isMyTurn || gameEnded);
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
            
            // Send move to server
            String moveCommand = "move-" + gameId + "-" + position + "-" + currentUsername;
            ClientMain.getClient().sendMessage(moveCommand);
            
            // The UI will be updated when the server confirms the move
            System.out.println("Sent move: " + moveCommand);
            
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

        // Re-enable board ONLY if it's now our turn (opponent just moved)
        if ((playerWhoMoved.equals(player1) && currentUsername.equals(player2)) ||
            (playerWhoMoved.equals(player2) && currentUsername.equals(player1))) {
            System.out.println("Re-enabling board - opponent moved, now our turn");
            board3x3.setDisable(false);
            System.out.println("Re-enabling successful");
        } else {
            board3x3.setDisable(true);
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

    public void addChatMessage(String message) {
        Platform.runLater(() -> {
            if (chatArea != null) {
                chatArea.getItems().add(message);
                // Auto-scroll to bottom
                chatArea.scrollTo(chatArea.getItems().size() - 1);
            }
        });
    }

    private void moveFromServer(int move) {
        //missing processing of server response

        updateBoard(move);
        board3x3.setDisable(false);
    }

    private void updateBoard(int move) {

        switch(symbol){
            case "X":
                if(turn%2==1){
                    grids.get(move).setImage(symbolX);
                }
                else grids.get(move).setImage(symbolO);
                break;
            case "O":
                if(turn%2==1){
                    grids.get(move).setImage(symbolO);
                }
                else grids.get(move).setImage(symbolX);
                break;
        }
        turn++;
    }

    // Method called when game ends (from server message)
    public void handleGameEnd(String endReason, String winner) {
        Platform.runLater(() -> {
            System.out.println("Game ended: " + endReason + ", Winner: " + winner);
            
            // Stop the timer
            if (gameTimer != null) {
                gameTimer.stop();
            }
            
            // Disable the board
            if (board3x3 != null) {
                board3x3.setDisable(true);
            }
            
            // Show game end message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            
            String currentUsername = ClientMain.getClient() != null ? ClientMain.getClient().getUsername() : "";
            String player1 = ClientMain.getCurrentPlayer1();
            String player2 = ClientMain.getCurrentPlayer2();
            
            if (endReason.equals("WIN")) {
                if (winner.equals(currentUsername)) {
                    alert.setHeaderText("You Won!");
                    alert.setContentText("Congratulations! You have won the game.");
                } else if (winner.equals("NONE")) {
                    alert.setHeaderText("Game Tied");
                    alert.setContentText("The game ended in a tie.");
                } else {
                    alert.setHeaderText("You Lost");
                    alert.setContentText(winner + " has won the game.");
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


    @FXML
    private void backToLobbyScene() {
        try {
            // Stop any ongoing processes
            if (gameTimer != null) {
                gameTimer.stop();
            }
            
            // Load the lobby scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bumba/tic_tac_toe/lobbyScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Get the current stage
            Stage stage = (Stage) board3x3.getScene().getWindow();
            
            // Set the new scene
            stage.setScene(scene);
            stage.setTitle("Tic Tac Toe - Lobby");
            stage.show();
            
            System.out.println("Returned to lobby scene");
        } catch (IOException e) {
            System.err.println("Failed to load lobby scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeTimer() {
        try {
            if (timerLabel != null) {
                timerLabel.setText("Time: 00:00");
                
                // Start timer
                Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1), event -> {
                        String[] parts = timerLabel.getText().split(":");
                        int minutes = Integer.parseInt(parts[1].split(":")[0].trim());
                        int seconds = Integer.parseInt(parts[1].split(":")[1].trim());
                        
                        seconds++;
                        if (seconds >= 60) {
                            minutes++;
                            seconds = 0;
                        }
                        
                        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
                    })
                );
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }
        } catch (Exception e) {
            System.err.println("Error initializing timer: " + e.getMessage());
        }
    }

    private void updateTimerDisplay() {
        if (timerLabel != null) {
            int minutes = secondsElapsed / 60;
            int seconds = secondsElapsed % 60;
            timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        }
    }

    // Stop timer
    public void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
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
}
