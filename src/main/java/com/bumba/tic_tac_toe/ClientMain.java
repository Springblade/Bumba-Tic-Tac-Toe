package com.bumba.tic_tac_toe;

import java.io.IOException;

import com.bumba.tic_tac_toe.client.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
            client = new Client("localhost", 5000, instance::onMessageReceived);
            client.startClient();
            System.out.println("Connected to server successfully");
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }


    public static void login(String username, String password) {
        if (client != null) {
            client.login(username, password);
        }
    }

    public static void register(String username, String password) {
        if (client != null) {
            client.register(username, password);
        }
    }

    public void sendRequestToServer( String type, String request) {
        //send request to server with specified type for init of game
    }

    public static void requestRankings() {
        if (client != null) {
            client.requestRankings();
        }
    }

    public static void sendGameMove(String moveIndex) {
        if (client != null) {
            client.sendGameMove(moveIndex);
        }
    }

    public static void sendJoinGame(String gameID) {
        if (client != null) {
            client.sendJoinGame(gameID);
        }
    }

    public static void sendChatMessage(String message) {
        if (client != null) {
            client.sendMessage("chat-" + message);
            System.out.println("Sending chat message: " + message);
        } else {
            System.err.println("Client not connected - cannot send chat message");
        }
    }

    public void getGameList() {
        client.getGameList();
    }
    public void getRankList() {
        client.getRankList();
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
        if (client != null) {
            client.sendMessage("get_player_info-" + username);
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
        if (username.equals(currentPlayer1)) {
            return currentPlayer2;
        } else if (username.equals(currentPlayer2)) {
            return currentPlayer1;
        }
        return null;
    }

    private void onMessageReceived(String message) {
        System.out.println("Message received in ClientMain: " + message);

        // Parse message tag and content
        String[] parts = message.split("-", 2);
        String tag = parts[0];
        String content = parts.length > 1 ? parts[1] : "";

        // Handle different types of messages based on tags
        switch (tag) {
            case "SERVER":
            case "AUTH_REQUEST":
                System.out.println("Server message: " + content);
                break;
            case "LOGIN_SUCCESS":
                System.out.println("Login successful: " + content);
                // Navigate to lobby scene here
                Platform.runLater(() -> authenController.transitionToLobbyScene());
                break;
            case "LOGIN_FAILED":
                authenController.handleError(content);
                break;
            case "REGISTER_SUCCESS":
                System.out.println("Registration successful: " + content);
                break;
            case "REGISTER_FAILED":
                authenController.handleError(content);
                break;

            case "GAME_CREATED":
                System.out.println("Game created successfully with ID: " + content);
                ClientMain.setCurrentGameInfo(content, ClientMain.getCurrentGameDimension());
                // Request creator's player info when game is created
                if (client != null) {
                    ClientMain.requestPlayerInfo(client.getUsername());
                }
                
                // Immediately transition creator to game scene
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.transitionToGame());
                }
                break;

            case "GAME_START":
                System.out.println("Game starting: " + content);
                String[] gameInfo = content.split(":");
                if (gameInfo.length >= 6) {
                    String gameId = gameInfo[0];
                    String player1 = gameInfo[1];
                    String player2 = gameInfo[2];
                    int dimension = Integer.parseInt(gameInfo[3]);
                    int player1Elo = Integer.parseInt(gameInfo[4]);
                    int player2Elo = Integer.parseInt(gameInfo[5]);
                    
                    // Store all game and player info
                    ClientMain.setCurrentGameInfo(gameId, dimension);
                    ClientMain.setCurrentGamePlayers(player1, player2, player1Elo, player2Elo);
                }
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.transitionToGame());
                }
                break;

            case "PLAYER_INFO":
                System.out.println("Received player info: " + content);
                // Format: "username:elo"
                String[] playerInfo = content.split(":");
                if (playerInfo.length >= 2) {
                    String username = playerInfo[0];
                    int elo = Integer.parseInt(playerInfo[1]);
                    
                    // Store the creator's info when game is created
                    if (client != null && username.equals(client.getUsername())) {
                        // This is the creator's info
                        ClientMain.setCurrentGamePlayers(username, null, elo, 0);
                    }
                    
                    // // Update game controller if it exists
                    // if (gameController != null) {
                    //     gameController.updatePlayerInfo(username, elo);
                    // }
                }
                break;
                
            case "GAMES_LIST":
                System.out.println("Received game list from server: " + content);
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.updateGameListFromServer(content));
                }
                break;

            case "GAME_END":
                System.out.println("Game ended: " + content);
                if (gameController != null) {
                    Platform.runLater(() -> {
                        gameController.handleGameEnd(content);
                        // Optionally transition back to lobby
                        // gameController.transitionBackToLobby();
                    });
                }
                break;

            case "LEAVE_SUCCESS":
                System.out.println("Successfully left game: " + content);
                // PLaceholder for handling leave success
                // Return to lobby when manually leaving
                // if (gameController != null) {
                //     Platform.runLater(() -> gameController.transitionBackToLobby());
                // }
                // break;
                
            case "SPECTATE_SUCCESS":
                System.out.println("Successfully joined as spectator: " + content);
                // Extract game dimension from spectate success message
                // Format: "Now spectating game gameId dimension"
                String[] spectateInfo = content.split(" ");
                if (spectateInfo.length >= 4) {
                    String gameId = spectateInfo[3];
                    // Request game info to get dimension
                    ClientMain.getClient().sendMessage("get_game_info-" + gameId);
                }
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.transitionToGame());
                }
                break;
                
            case "SPECTATOR_JOIN":
                System.out.println("Spectator joined: " + content);
                // Placeholder for handling spectator joining
                break;
                
            case "SPECTATOR_LEAVE":
                System.out.println("Spectator left: " + content);
                // placeholder for handling spectator leaving
                break;
                
            case "NO_GAMES":
                System.out.println("No games available on server");
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.clearGameList());
                }
                break;
            case "RANKINGS_LIST":
                System.out.println("Received rankings from server: " + content);
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.updateRankingsFromServer(content));
                }
                break;
            case "NO_RANKINGS":
                System.out.println("No rankings available on server");
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.clearRankings());
                }
                break;
            case "GAME_AVAILABLE":
                System.out.println("New game available in lobby: " + content);
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.addGameToLobby(content));
                }
                break;
            case "GAME_REMOVED":
                System.out.println("Game removed from lobby: " + content);
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.removeGameFromLobby(content));
                }
                break;
            case "CHAT":
                System.out.println("Chat message: " + content);
                if (gameController != null) {
                    Platform.runLater(() -> gameController.addChatMessage(content));
                }
                break;

            case "CHAT_HISTORY":
                System.out.println("Received chat history: " + content);
                if (gameController != null) {
                    String[] messages = content.split("-");
                    Platform.runLater(() -> {
                        for (String chatMessage : messages) {
                            if (!chatMessage.isEmpty()) {
                                gameController.addChatMessage(chatMessage);
                            }
                        }
                    });
                }
                break;

            case "CHAT_ACK":
                System.out.println("Chat message sent successfully");
                break;
                
            case "GAME_MOVE":
                System.out.println("Game move received: " + content);
                if (gameController != null) {
                    // Parse move data: gameId:username:position:nextTurn
                    String[] moveData = content.split(":");
                    if (moveData.length >= 4) {
                        try {
                            int position = Integer.parseInt(moveData[2]);
                            String playerWhoMoved = moveData[1];
                            
                            Platform.runLater(() -> {
                                gameController.handleOpponentMove(position, playerWhoMoved);
                            });
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid move position: " + moveData[2]);
                        }
                    }
                }
                break;
            case "USER_JOIN":
            case "USER_LEAVE":
            case "USER_JOIN_GAME":
                System.out.println("User notification: " + content);
                break;
            case "ERROR":
                System.err.println("Server error: " + content);
                break;
            case "CONNECTION_LOST":
                System.err.println("Connection to server lost");
                break;
            case "CONNECTION_ERROR":
                System.err.println("Connection error occurred");
                break;
            default:
                System.out.println("Unknown message: " + message);
                break;
        }
    }

    public static Client getClient() {
        return client;
    }


    public static void main(String[] args) {
        launch();
    }



}