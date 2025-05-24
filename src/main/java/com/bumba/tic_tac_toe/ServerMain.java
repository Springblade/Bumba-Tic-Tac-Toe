package com.bumba.tic_tac_toe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bumba.tic_tac_toe.server.GamesManager;
import com.bumba.tic_tac_toe.server.clientHandler;

public class ServerMain {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 50;
    private static List<clientHandler> clients = new ArrayList<>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
    private static ServerSocket serverSocket;
    private static GamesManager gamesManager = new GamesManager();

    public static void main(String[] args) {
        System.out.println("Starting Tic Tac Toe Server on port " + PORT + "...");

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started successfully! Waiting for clients...");

            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(ServerMain::shutdown));

            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());

                    // Create and start client handler
                    clientHandler clientThread = new clientHandler(clientSocket, clients);
                    synchronized (clients) {
                        clients.add(clientThread);
                    }

                    // Submit to thread pool instead of creating new thread
                    threadPool.submit(clientThread);

                    System.out.println("Total connected clients: " + clients.size());

                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private static void shutdown() {
        System.out.println("Shutting down server...");

        try {
            // Close server socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            // Shutdown thread pool
            threadPool.shutdown();

            // Close all client connections
            synchronized (clients) {
                for (clientHandler client : clients) {
                    client.sendMessage("SERVER-Server is shutting down");
                }
                clients.clear();
            }

            System.out.println("Server shutdown complete");
        } catch (IOException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    public static List<clientHandler> getClients() {
        return clients;
    }

    public static GamesManager getGamesManager() {
        return gamesManager;
    }

    // Broadcast to all clients (lobby)
    public static void broadcastToAll(String message, clientHandler sender) {
        synchronized (clients) {
            for (clientHandler client : clients) {
                if (client != sender && client.isAuthenticated()) {
                    client.sendMessage(message);
                }
            }
        }
    }

    // Broadcast to clients in the same game (excluding sender)
    public static void broadcastToGame(String gameId, String message, clientHandler sender) {
        synchronized (clients) {
            for (clientHandler client : clients) {
                if (client != sender && client.isAuthenticated() && client.isInGame(gameId)) {
                    client.sendMessage(message);
                }
            }
        }
    }

    // Broadcast to all players in a specific game (including sender)
    public static void broadcastToGamePlayers(String gameId, String message) {
        synchronized (clients) {
            for (clientHandler client : clients) {
                if (client.isAuthenticated() && client.isInGame(gameId)) {
                    client.sendMessage(message);
                }
            }
        }
    }

    // Broadcast to spectators of a game
    public static void broadcastToSpectators(String gameId, String message) {
        synchronized (clients) {
            for (clientHandler client : clients) {
                if (client.isAuthenticated() && client.isSpectating(gameId)) {
                    client.sendMessage(message);
                }
            }
        }
    }

    // Broadcast to both players and spectators in a game
    public static void broadcastToGameSession(String gameId, String message) {
        synchronized (clients) {
            for (clientHandler client : clients) {
                if (client.isAuthenticated() &&
                        (client.isInGame(gameId) || client.isSpectating(gameId))) {
                    client.sendMessage(message);
                }
            }
        }
    }

    public static void removeClient(clientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }
}