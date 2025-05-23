package com.bumba.tic_tac_toe.client;

import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> onMessageReceived;
    private String[][] board;

    public Client(String serverAddress, int serverPort, Consumer<String> onMessageReceived) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.onMessageReceived = onMessageReceived;
    }


    public String processJoinMsg(String msg, String gameID){
        return switch (msg) {
            case "join" -> "join-" + gameID;
            case "spectate" -> "spectate-" + gameID;
            default -> "Buh";
        };
    }

    public String processGameMsg(String msg) {
        return "game-"+msg;
    }

    public String processChatMsg(String msg) {
        return "chat-"+msg;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public interface MessageListener {
        void onMessageReceived(String message);
    }


    public void startClient() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                        String finalLine = line.trim();
                        Platform.runLater(() -> onMessageReceived.accept(finalLine));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}