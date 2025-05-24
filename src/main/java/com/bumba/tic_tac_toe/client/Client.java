package com.bumba.tic_tac_toe.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Consumer;

import javafx.application.Platform;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> onMessageReceived;
//    private String[][] board;
    private boolean isConnected = false;
    private Thread readerThread;
    private String username;

    public Client(String serverAddress, int serverPort, Consumer<String> onMessageReceived) throws IOException {
        this.onMessageReceived = onMessageReceived;
        connect(serverAddress, serverPort);
    }

    private void connect(String serverAddress, int serverPort) throws IOException {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.isConnected = true;

            System.out.println("Connected to server: " + serverAddress + ":" + serverPort);
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            throw e;
        }
    }

    public void startClient() {
        if (!isConnected) {
            System.err.println("Not connected to server");
            return;
        }

        readerThread = new Thread(() -> {
            try {
                String line;
                while (isConnected && (line = in.readLine()) != null) {
                    final String finalLine = line.trim();
                    System.out.println("Received: " + finalLine);

                    // Process the message and handle different tags
                    Platform.runLater(() -> handleServerMessage(finalLine));
                }
            } catch (SocketException e) {
                if (isConnected) {
                    System.err.println("Connection lost: " + e.getMessage());
                    Platform.runLater(() -> onMessageReceived.accept("CONNECTION_LOST"));
                }
            } catch (IOException e) {
                if (isConnected) {
                    System.err.println("Error reading from server: " + e.getMessage());
                    Platform.runLater(() -> onMessageReceived.accept("CONNECTION_ERROR"));
                }
            }
        });

        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void handleServerMessage(String message) {
        String[] parts = message.split("-", 2);
        String tag = parts[0];

        switch (tag) {
            case "SERVER":
            case "AUTH_REQUEST":
            case "LOGIN_SUCCESS":
            case "LOGIN_FAILED":
            case "REGISTER_SUCCESS":
            case "REGISTER_FAILED":
            case "ERROR":
            case "MOVE_ACK":
            case "JOIN_ACK":
            case "CHAT_ACK":
                onMessageReceived.accept(message);
                break;
            case "GAME_MOVE":
                onMessageReceived.accept(message);
                break;
            case "CHAT":
                onMessageReceived.accept(message);
                break;
            case "USER_JOIN":
            case "USER_LEAVE":
            case "USER_JOIN_GAME":
                onMessageReceived.accept(message);
                break;
            case "PONG":
                System.out.println("Ping successful");
                break;
            default:
                onMessageReceived.accept(message);
                break;
        }
    }

    // Preprocess outgoing messages with proper tags
    public void sendMessage(String msg) {
        if (isConnected && out != null) {
            out.println(msg);
            System.out.println("Sent: " + msg);
        } else {
            System.err.println("Cannot send message: not connected");
        }
    }

    // Authentication methods with proper message preprocessing
    public void login(String username, String password) {
        this.username = username;
        String loginMessage = preprocessMessage("login", username + "-" + password);
        sendMessage(loginMessage);
    }

    public void register(String username, String password) {
        String registerMessage = preprocessMessage("register", username + "-" + password);
        sendMessage(registerMessage);
    }

    // Game-related message processing methods with proper tags
    public String processJoinMsg(String msg, String gameID) {
        if (username == null) {
            System.err.println("Username not set");
            return null;
        }

        return switch (msg) {
            case "join" -> preprocessMessage("join", username + "-" + gameID);
            case "spectate" -> preprocessMessage("spectate", username + "-" + gameID);
            default -> null;
        };
    }

    public String processGameMsg(String moveIndex) {
        if (username == null) {
            System.err.println("Username not set");
            return null;
        }
        return preprocessMessage("move", username + "-" + moveIndex);
    }

    public String processChatMsg(String content) {
        if (username == null) {
            System.err.println("Username not set");
            return null;
        }
        return preprocessMessage("chat", username + ":" + content);
    }

    // Helper method to preprocess outgoing messages
    private String preprocessMessage(String tag, String content) {
        return tag + "-" + content;
    }

    // Send game move
    public void sendGameMove(String moveIndex) {
        String moveMessage = processGameMsg(moveIndex);
        if (moveMessage != null) {
            sendMessage(moveMessage);
        }
    }

    // Send join game request
    public void sendJoinGame(String gameID) {
        String joinMessage = processJoinMsg("join", gameID);
        if (joinMessage != null) {
            sendMessage(joinMessage);
        }
    }

    // Send chat message
    public void sendChatMessage(String content) {
        String chatMessage = processChatMsg(content);
        if (chatMessage != null) {
            sendMessage(chatMessage);
        }
    }

    // Connection management
    public void ping() {
        sendMessage("ping");
    }

    public void logout() {
        sendMessage("logout");
    }

    public void disconnect() {
        isConnected = false;

        try {
            if (readerThread != null && readerThread.isAlive()) {
                readerThread.interrupt();
            }

            if (out != null) {
                out.close();
            }

            if (in != null) {
                in.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

//    public String[][] getBoard() {
//        return board;
//    }

//    public void setBoard(String[][] board) {
//        this.board = board;
//    }

    public String getUsername() {
        return username;
    }
}