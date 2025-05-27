package com.bumba.tic_tac_toe;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.bumba.tic_tac_toe.client.Client;
import com.bumba.tic_tac_toe.database.Connect;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ClientMain extends Application {

    private static Client client;
    private static ClientMain instance;
    private static AuthenController authenController;
    private static LobbyController lobbyController;
    private static GameController gameController;
    private static String currentGameId;
    private static int currentGameDimension = 3;
    private static String currentPlayer1;
    private static String currentPlayer2;
    private static int currentPlayer1Elo;
    private static int currentPlayer2Elo;
    
    // Add connection status property
    private static BooleanProperty connectedProperty = new SimpleBooleanProperty(false);

    public static BooleanProperty connectedProperty() {
        return connectedProperty;
    }

    public static boolean isConnected() {
        return connectedProperty.get();
    }

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;

        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("/com/bumba/tic_tac_toe/logInScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        authenController = fxmlLoader.getController();
        stage.setResizable(false);
        stage.setTitle("Tic Tac Toe");
        stage.setScene(scene);

        // Connect to server when application starts
        connect();

        // Handle application close
        stage.setOnCloseRequest(event -> {
            if (client != null) {
                client.disconnect();
            }
            Platform.exit();
        });

        stage.show();
    }
    
    public static void setLobbyController(LobbyController controller) {
        lobbyController = controller;
    }

    public static void setGameController(GameController controller) {
        gameController = controller;
    }

    public static GameController getGameController() {
        return gameController;
    }

    public static void setCurrentGameInfo(String gameId, int dimension) {
        currentGameId = gameId;
        currentGameDimension = dimension;
    }

    public static int getCurrentGameDimension() {
        return currentGameDimension;
    }

    public static String getCurrentGameId() {
        return currentGameId;
    }

    public static void connect() {
        try {
            // Check if already connected
            if (client != null && client.isConnected()) {
                System.out.println("Already connected to server");
                connectedProperty.set(true);
                return;
            }
            
            System.out.println("Connecting to server...");
            // Create client with message handler
            client = new Client("localhost", 5000, ClientMain::processMessage);
            
            // Start client in a separate thread
            Thread clientThread = new Thread(() -> {
                try {
                    client.startClient();
                } catch (Exception e) {
                    System.err.println("Client error: " + e.getMessage());
                    Platform.runLater(() -> {
                        connectedProperty.set(false);
                        showConnectionError("Connection Error", 
                            "Failed to connect to server: " + e.getMessage());
                    });
                }
            });
            clientThread.setDaemon(true);
            clientThread.start();
            
            // Set connection status
            connectedProperty.set(true);
            System.out.println("Connected to server successfully");
            
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
            connectedProperty.set(false);
            showConnectionError("Connection Error", 
                "Failed to connect to server: " + e.getMessage());
        }
    }

    private static void showConnectionError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void login(String username, String password) {
        if (client != null && client.isConnected()) {
            client.login(username, password);
        } else {
            System.err.println("Cannot login: client not connected");
            showConnectionError("Connection Error", "Not connected to server. Please restart the application.");
        }
    }

    public static void register(String username, String password) {
        if (client != null && client.isConnected()) {
            client.register(username, password);
        } else {
            System.err.println("Cannot register: client not connected");
            showConnectionError("Connection Error", "Not connected to server. Please restart the application.");
        }
    }

    public void sendRequestToServer( String type, String request) {
        //send request to server with specified type for init of game
    }

    public static void requestRankings() {
        if (client != null && client.isConnected()) {
            client.requestRankings();
        } else {
            System.err.println("Cannot request rankings: client not connected");
        }
    }

    public static void sendJoinGame(String gameID) {
        if (client != null && client.isConnected()) {
            client.sendJoinGame(gameID);
        } else {
            System.err.println("Cannot join game: client not connected");
            showConnectionError("Connection Error", "Not connected to server. Please restart the application.");
        }
    }

    public void getGameList() {
        if (client != null && client.isConnected()) {
            client.getGameList();
        } else {
            System.err.println("Cannot get game list: client not connected");
        }
    }

    public void getRankList() {
        if (client != null && client.isConnected()) {
            client.getRankList();
        } else {
            System.err.println("Cannot get rank list: client not connected");
        }
    }

    public static void setCurrentGamePlayers(String player1, String player2, int player1Elo, int player2Elo) {
        currentPlayer1 = player1;
        currentPlayer2 = player2;
        currentPlayer1Elo = player1Elo;
        currentPlayer2Elo = player2Elo;
        
        System.out.println("Game players set:");
        System.out.println("Player 1: " + player1 + " (ELO: " + player1Elo + ")");
        System.out.println("Player 2: " + player2 + " (ELO: " + player2Elo + ")");
    }

    public static String getCurrentPlayer1() {
        return currentPlayer1;
    }

    public static String getCurrentPlayer2() {
        return currentPlayer2;
    }

    public static int getCurrentPlayer1Elo() {
        return currentPlayer1Elo;
    }

    public static int getCurrentPlayer2Elo() {
        return currentPlayer2Elo;
    }
    
    // Method to request player info from server
    public static void requestPlayerInfo(String username) {
        if (client != null && client.isConnected()) {
            client.requestPlayerInfo(username);
        } else {
            System.err.println("Cannot request player info: client not connected");
        }
    }

    // Method to determine current player's role
    public static boolean isPlayer1() {
        return client != null && currentPlayer1 != null && 
            currentPlayer1.equals(client.getUsername());
    }

    public static String getCurrentPlayerSymbol() {
        return isPlayer1() ? "X" : "O";
    }

    public static String getOpponentName() {
        if (client == null) return null;
        String username = client.getUsername();
        if (username == null) return null;
        
        if (username.equals(currentPlayer1)) {
            return currentPlayer2;
        } else if (username.equals(currentPlayer2)) {
            return currentPlayer1;
        }
        return null;
    }

    // Handle game move messages from server
    private static void handleGameMove(String content) {
        try {
            System.out.println("Received game move: " + content);
            
            // Parse move data: gameId:playerWhoMoved:position:nextTurn
            String[] parts = content.split(":");
            if (parts.length < 4) {
                System.err.println("Invalid game move format: " + content);
                return;
            }
            
            String gameId = parts[0];
            String playerWhoMoved = parts[1];
            int position = Integer.parseInt(parts[2]);
            String nextTurn = parts[3];
            
            // Update current game state
            currentGameId = gameId;
            
            // Update UI if game controller is available
            if (gameController != null) {
                Platform.runLater(() -> {
                    try {
                        // If it's not our move, update the board with opponent's move
                        String currentUsername = client != null ? client.getUsername() : "";
                        if (!playerWhoMoved.equals(currentUsername)) {
                            gameController.handleOpponentMove(position, playerWhoMoved);
                        }
                        
                        // Update turn indicator
                        gameController.updateTurnIndicator(nextTurn);
                    } catch (Exception e) {
                        System.err.println("Error updating UI for game move: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else {
                System.err.println("Game controller is null, can't update UI");
            }
        } catch (Exception e) {
            System.err.println("Error handling game move: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Send a game move to the server
    public static void sendGameMove(String position) {
        try {
            if (client == null) {
                System.err.println("Cannot send move: client is null");
                return;
            }
            
            if (!client.isConnected()) {
                System.err.println("Cannot send move: client is not connected");
                return;
            }
            
            String username = client.getUsername();
            if (username == null || username.isEmpty()) {
                System.err.println("Cannot send move: not authenticated");
                return;
            }
            
            // Format: move-username-position
            client.sendGameMove(position);
            System.out.println("Sent move: position " + position);
        } catch (Exception e) {
            System.err.println("Error sending game move: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handle game end messages from server
    private static void handleGameEnd(String content) {
        try {
            System.out.println("Received game end: " + content);
            
            String[] parts = content.split(":");
            if (parts.length < 3) {
                System.err.println("Invalid game end format: " + content);
                return;
            }
            
            String gameId = parts[0];
            String endReason = parts[1];
            String winner = parts[2];
            
            // Update UI if game controller is available
            if (gameController != null) {
                Platform.runLater(() -> {
                    try {
                        gameController.handleGameEnd(endReason, winner);
                    } catch (Exception e) {
                        System.err.println("Error updating UI for game end: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else {
                System.err.println("Game controller is null, can't update UI for game end");
            }
        } catch (Exception e) {
            System.err.println("Error handling game end: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void processMessage(String message) {
        try {
            System.out.println("Received: " + message);
            
            // Parse message
            String[] parts = message.split("-", 2);
            String tag = parts[0];
            String content = parts.length > 1 ? parts[1] : "";
            
            switch (tag) {
                case "AUTH_OK":
                case "LOGIN_SUCCESS":
                    Platform.runLater(() -> {
                        try {
                            // Load lobby scene
                            FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/com/bumba/tic_tac_toe/lobby.fxml"));
                            Scene scene = new Scene(loader.load(), 800, 600);
                            
                            // Get controller
                            lobbyController = loader.getController();
                            
                            // Show lobby
                            Stage stage = (Stage) authenController.getScene().getWindow();
                            stage.setScene(scene);
                            stage.setTitle("Tic Tac Toe - Lobby");
                        } catch (Exception e) {
                            System.err.println("Error loading lobby: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    break;
                    
                case "AUTH_FAIL":
                case "LOGIN_FAILED":
                    Platform.runLater(() -> {
                        authenController.handleError("Authentication failed: " + content);
                    });
                    break;
                    
                case "REGISTER_OK":
                case "REGISTER_SUCCESS":
                    Platform.runLater(() -> {
                        authenController.handleError("Registration successful! You can now log in.");
                    });
                    break;
                    
                case "REGISTER_FAIL":
                case "REGISTER_FAILED":
                    Platform.runLater(() -> {
                        authenController.handleError("Registration failed: " + content);
                    });
                    break;
                    
                case "GAME_START":
                    handleGameStart(content);
                    break;
                    
                case "GAME_MOVE":
                    handleGameMove(content);
                    break;
                    
                case "CHAT":
                    handleGameChat(content);
                    break;
                    
                case "GAME_END":
                    handleGameEnd(content);
                    break;
                    
                case "ERROR":
                    System.err.println("Server error: " + content);
                    break;
                    
                case "GAMES_LIST":
                    if (lobbyController != null) {
                        Platform.runLater(() -> lobbyController.updateGamesList(content));
                    }
                    break;
                    
                case "RANKINGS_LIST":
                    if (lobbyController != null) {
                        Platform.runLater(() -> lobbyController.updateRankingsList(content));
                    }
                    break;
                    
                case "PLAYER_INFO":
                    // Handle player info response
                    break;
                    
                case "CONNECTION_LOST":
                    Platform.runLater(() -> {
                        connectedProperty.set(false);
                        showConnectionError("Connection Lost", "Lost connection to the server. Please restart the application.");
                    });
                    break;
                    
                default:
                    System.out.println("Unknown message: " + message);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleGameStart(String content) {
        try {
            // Parse game start data
            String[] parts = content.split("-");
            if (parts.length < 6) {
                System.err.println("Invalid game start data: " + content);
                return;
            }
            
            String gameId = parts[0];
            int dimension = Integer.parseInt(parts[1]);
            String player1 = parts[2];
            String player2 = parts[3];
            int elo1 = Integer.parseInt(parts[4]);
            int elo2 = Integer.parseInt(parts[5]);
            
            // Store game info
            currentGameId = gameId;
            currentGameDimension = dimension;
            setCurrentGamePlayers(player1, player2, elo1, elo2);
            
            // Load game scene
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/com/bumba/tic_tac_toe/game.fxml"));
                    Scene scene = new Scene(loader.load(), 800, 600);
                    
                    // Get controller
                    gameController = loader.getController();
                    
                    // Show game
                    Stage stage = (Stage) lobbyController.getScene().getWindow();
                    stage.setScene(scene);
                    stage.setTitle("Tic Tac Toe - Game");
                } catch (Exception e) {
                    System.err.println("Error loading game: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error handling game start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleGameChat(String content) {
        try {
            // Check if we have a game controller
            if (gameController == null) {
                System.err.println("Cannot handle chat: game controller is null");
                return;
            }
            
            // Add message to chat area
            Platform.runLater(() -> {
                gameController.addChatMessage(content);
            });
        } catch (Exception e) {
            System.err.println("Error handling chat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Client getClient() {
        return client;
    }


    public static void main(String[] args) {
        launch();
    }



}
