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

    public static void sendChatMessage(String content) {
        if (client != null) {
            client.sendChatMessage(content);
        }
    }

    public void getGameList() {
        client.getGameList();
    }
    public void getRankList() {
        client.getRankList();
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
                // Immediately transition creator to game scene
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.transitionToGame());
                }
                break;

            case "GAME_START":
                System.out.println("Game starting: " + content);
                // Transition both players to game scene
                String[] gameInfo = content.split(":");
                if (gameInfo.length >= 4) {
                    String gameId = gameInfo[0];
                    int dimension = Integer.parseInt(gameInfo[3]);
                    ClientMain.setCurrentGameInfo(gameId, dimension);
                }
                if (lobbyController != null) {
                    Platform.runLater(() -> lobbyController.transitionToGame());
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
                // Placeholder for handling game end
                // Transition back to lobby when game ends
                // if (gameController != null) {
                //     Platform.runLater(() -> gameController.handleGameEnd(content));
                // }
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
                break;
            case "GAME_MOVE":
                System.out.println("Game move: " + content);

                // Update game board here

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