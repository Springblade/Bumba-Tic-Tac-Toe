package com.bumba.tic_tac_toe.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.bumba.tic_tac_toe.ServerMain;
import com.bumba.tic_tac_toe.database.Create;
import com.bumba.tic_tac_toe.database.LogIn;

public class clientHandler implements Runnable {
    private Socket clientSocket;
    private List<clientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;
    private String clientUsername;
    private boolean isAuthenticated = false;

    public clientHandler(Socket socket, List<clientHandler> clients) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            // Send welcome message
            sendMessage("SERVER: Connected to Tic Tac Toe Server");
            sendMessage("AUTH_REQUEST: Please authenticate");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from " + getClientId() + ": " + inputLine);
                
                // Preprocess and handle message based on tag
                handleMessage(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Client " + getClientId() + " disconnected: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void handleMessage(String message) {
        String[] parts = message.split("-", 3); // Split into max 3 parts
        String tag = parts[0].toLowerCase();

        try {
            switch (tag) {
                case "login":
                    handleLogin(parts);
                    break;
                case "register":
                    handleRegister(parts);
                    break;
                case "move":
                    handleGameMove(parts);
                    break;
                case "join":
                    handleJoinGame(parts);
                    break;
                case "chat":
                    handleChatMessage(parts);
                    break;
                case "ping":
                    handlePing();
                    break;
                case "logout":
                    handleLogout();
                    break;
                default:
                    sendMessage("ERROR: Unknown command: " + tag);
                    break;
            }
        } catch (Exception e) {
            sendMessage("ERROR: Invalid message format");
            System.err.println("Error processing message from " + getClientId() + ": " + e.getMessage());
        }
    }

    private void handleLogin(String[] parts) {
        if (parts.length < 3) {
            sendMessage("ERROR: Invalid login format. Use: login-username-password");
            return;
        }
        
        String username = parts[1];
        String password = parts[2];
        
        String loginResult = LogIn.loginAccount(username, password);
        if (loginResult.equals("Success")) {
            this.clientUsername = username;
            this.isAuthenticated = true;
            sendMessage("LOGIN_SUCCESS: Welcome " + username);
            
            // Broadcast to other clients
            String broadcastMsg = preprocessServerMessage("USER_JOIN", username + " joined the server");
            ServerMain.broadcastToAll(broadcastMsg, this);
        } else {
            sendMessage("LOGIN_FAILED: " + loginResult);
        }
    }

    private void handleRegister(String[] parts) {
        if (parts.length < 3) {
            sendMessage("ERROR: Invalid register format. Use: register-username-password");
            return;
        }
        
        String username = parts[1];
        String password = parts[2];
        
        if (Create.unique(username)) {
            Create.insert(username, password);
            sendMessage("REGISTER_SUCCESS: Registration successful! You can now login.");
        } else {
            sendMessage("REGISTER_FAILED: Username already exists");
        }
    }

    private void handleGameMove(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR: Not authenticated");
            return;
        }
        
        if (parts.length < 3) {
            sendMessage("ERROR: Invalid move format. Use: move-username-index");
            return;
        }
        
        String username = parts[1];
        String moveIndex = parts[2];
        
        // Verify the username matches the authenticated user
        if (!username.equals(this.clientUsername)) {
            sendMessage("ERROR: Username mismatch");
            return;
        }
        
        // Broadcast the move to all other clients
        String moveMessage = preprocessServerMessage("GAME_MOVE", username + "-" + moveIndex);
        ServerMain.broadcastToAll(moveMessage, this);
        
        // Acknowledge the move
        sendMessage("MOVE_ACK: Move processed");
    }

    private void handleJoinGame(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR: Not authenticated");
            return;
        }
        
        if (parts.length < 3) {
            sendMessage("ERROR: Invalid join format. Use: join-username-gameID");
            return;
        }
        
        String username = parts[1];
        String gameID = parts[2];
        
        // Verify the username matches the authenticated user
        if (!username.equals(this.clientUsername)) {
            sendMessage("ERROR: Username mismatch");
            return;
        }
        
        // Process game join logic here (to be implemented later)
        sendMessage("JOIN_ACK: Attempting to join game " + gameID);
        
        // Broadcast to other clients
        String joinMessage = preprocessServerMessage("USER_JOIN_GAME", username + " joined game " + gameID);
        ServerMain.broadcastToAll(joinMessage, this);
    }

    private void handleChatMessage(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR: Not authenticated");
            return;
        }
        
        if (parts.length < 2) {
            sendMessage("ERROR: Invalid chat format. Use: chat-username:content");
            return;
        }
        
        // Parse username:content from the second part
        String userAndContent = parts[1];
        String[] userContentParts = userAndContent.split(":", 2);
        
        if (userContentParts.length < 2) {
            sendMessage("ERROR: Invalid chat format. Use: chat-username:content");
            return;
        }
        
        String username = userContentParts[0];
        String content = userContentParts[1];
        
        // Verify the username matches the authenticated user
        if (!username.equals(this.clientUsername)) {
            sendMessage("ERROR: Username mismatch");
            return;
        }
        
        // Broadcast the chat message to all clients
        String chatMessage = preprocessServerMessage("CHAT", username + ": " + content);
        ServerMain.broadcastToAll(chatMessage, this);
        
        // Echo back to sender for confirmation
        sendMessage("CHAT_ACK: Message sent");
    }

    private void handlePing() {
        sendMessage("PONG");
    }

    private void handleLogout() {
        if (isAuthenticated) {
            String logoutMessage = preprocessServerMessage("USER_LEAVE", clientUsername + " left the server");
            ServerMain.broadcastToAll(logoutMessage, this);
        }
        cleanup();
    }

    // Preprocess outgoing server messages with proper tags
    private String preprocessServerMessage(String tag, String content) {
        return tag + "-" + content;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void cleanup() {
        try {
            if (clientUsername != null && isAuthenticated) {
                String leaveMessage = preprocessServerMessage("USER_LEAVE", clientUsername + " left the server");
                ServerMain.broadcastToAll(leaveMessage, this);
            }
            
            synchronized (clients) {
                clients.remove(this);
            }
            
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            
            System.out.println("Client " + getClientId() + " cleanup completed");
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    private String getClientId() {
        return clientUsername != null ? clientUsername : clientSocket.getRemoteSocketAddress().toString();
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}