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

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;

        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("fxml/logInScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
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

    private void sendRequestToServer( String type, String request) {
        //send request to server with specified type for init of game
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

    public String[] getGameList() {
        if (client != null) {
            return client.getGameList();
        }
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
                break;
            case "LOGIN_FAILED":
                System.err.println("Login failed: " + content);
                break;
            case "REGISTER_SUCCESS":
                System.out.println("Registration successful: " + content);
                break;
            case "REGISTER_FAILED":
                System.err.println("Registration failed: " + content);
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