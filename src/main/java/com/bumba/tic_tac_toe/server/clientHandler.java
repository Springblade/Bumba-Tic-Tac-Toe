package com.bumba.tic_tac_toe.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.List;

import com.bumba.tic_tac_toe.ServerMain;
import com.bumba.tic_tac_toe.database.Create;
import com.bumba.tic_tac_toe.database.EloMod;
import com.bumba.tic_tac_toe.database.LogIn;
import com.bumba.tic_tac_toe.database.Rank;
import com.bumba.tic_tac_toe.game.TicTacToe;

public class clientHandler implements Runnable {
    private Socket clientSocket;
    private List<clientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;
    private String clientUsername;
    private boolean isAuthenticated = false;
    private String currentGameId = null;
    private boolean isSpectator = false;

    public clientHandler(Socket socket, List<clientHandler> clients) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() {
        try {
            // Send welcome message
            sendMessage("SERVER-Connected to Tic Tac Toe Server");
            sendMessage("AUTH_REQUEST-Please authenticate");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from " + getClientId() + ": " + inputLine);
                handleMessage(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Client " + getClientId() + " disconnected: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void handleMessage(String message) {
        String[] parts = message.split("-", 3);
        String tag = parts[0].toLowerCase();

        try {
            switch (tag) {
                case "login":
                    handleLogin(parts);
                    break;
                case "register":
                    handleRegister(parts);
                    break;
                case "create_game":
                    handleCreateGame(parts);
                    break;
                case "list_games":
                    handleListGames();
                    break;
                case "get_rankings":
                    handleGetRankings();
                    break;
                case "quick_join":
                    handleQuickJoin(parts);
                    break;
                case "join_game":
                    handleJoinGame(parts);
                    break;
                case "get_player_info":
                    handleGetPlayerInfo(parts);
                    break;
                case "spectate":
                    handleSpectate(parts);
                    break;
                case "move":
                    handleGameMove(parts);
                    break;
                case "chat":
                    handleChatMessage(parts);
                    break;
                case "leave_game":
                    handleLeaveGame();
                    break;
                case "ping":
                    sendMessage("PONG");
                    break;
                case "logout":
                    handleLogout();
                    break;
                default:
                    sendMessage("ERROR-Unknown command: " + tag);
                    break;
            }
        } catch (Exception e) {
            sendMessage("ERROR-Invalid message format");
            System.err.println("Error processing message from " + getClientId() + ": " + e.getMessage());
        }
    }

    private void handleListGames() {
        if (!isAuthenticated) {
            sendMessage("ERROR-Not authenticated");
            return;
        }
        
        Collection<TicTacToe> allGames = ServerMain.getGamesManager().getAllGames();
        if (allGames.isEmpty()) {
            sendMessage("NO_GAMES");
        } else {
            StringBuilder sb = new StringBuilder("GAMES_LIST");
            for (TicTacToe game : allGames) {
                // Format: gameId:creator:status
                String creator = game.getPlayer1();
                String status = game.getPlayer2() == null ? "WAITING" : "IN_PROGRESS";
                sb.append("-").append(game.getGameId()).
                    append(":").append(creator).
                    append(":").append(status).
                    append(":").append(String.valueOf(game.getDimension()));
            }
            sendMessage(sb.toString());
        }
    }

    private void handleGetPlayerInfo(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR-Not authenticated");
            return;
        }

        if (parts.length < 2) {
            sendMessage("ERROR-Invalid format. Use: get_player_info-username");
            return;
        }

        String requestedUsername = parts[1];
        
        // Get player's ELO from database
        int playerElo = EloMod.getCurrentElo(requestedUsername);
        
        // Send player info back to requesting client
        String playerInfo = "PLAYER_INFO-" + requestedUsername + ":" + playerElo;
        sendMessage(playerInfo);
    }

    private void handleGetRankings() {
        if (!isAuthenticated) {
            sendMessage("ERROR-Not authenticated");
            return;
        }
        
        List<String> rankings = Rank.Ranking();
        if (rankings.isEmpty()) {
            sendMessage("NO_RANKINGS");
        } else {
            StringBuilder sb = new StringBuilder("RANKINGS_LIST");
            for (String ranking : rankings) {
                sb.append("-").append(ranking);
            }
            sendMessage(sb.toString());
        }
    }    

    private void handleLogin(String[] parts) {
        if (parts.length < 3) {
            sendMessage("ERROR-Invalid login format. Use: login-username-password");
            return;
        }

        String username = parts[1];
        String password = parts[2];

        if (LogIn.loginAccount(username, password)) {
            this.clientUsername = username;
            this.isAuthenticated = true;
            sendMessage("LOGIN_SUCCESS-Welcome " + username);

            // Broadcast to lobby only
            String broadcastMsg = "USER_JOIN-" + username + " joined the server";
            ServerMain.broadcastToAll(broadcastMsg, this);
        } else {
            sendMessage("LOGIN_FAILED-");
        }
    }

    private void handleRegister(String[] parts) {
        if (parts.length < 3) {
            sendMessage("ERROR-Invalid register format. Use: register-username-password");
            return;
        }

        String username = parts[1];
        String password = parts[2];

        if (Create.unique(username)) {
            Create.insert(username, password);
            sendMessage("REGISTER_SUCCESS-Registration successful! You can now login.");
        } else {
            sendMessage("REGISTER_FAILED-Username already exists");
        }
    }

    private void handleCreateGame(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR-Not authenticated");
            return;
        }

        TicTacToe game = ServerMain.getGamesManager().newGame(clientUsername);
        game.setDimension(Integer.parseInt(parts[2])); 
        this.currentGameId = game.getGameId();
        this.isSpectator = false;

        sendMessage("GAME_CREATED-" + game.getGameId());

        // Broadcast new game to lobby
        String gameInfo = "GAME_AVAILABLE-" + game.getGameId() + ":" + clientUsername + ":WAITING";
        ServerMain.broadcastToAll(gameInfo, this);
    }

    private void handleQuickJoin(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR-Not authenticated");
            return;
        }

        TicTacToe game = ServerMain.getGamesManager().quickJoin(clientUsername);
        this.currentGameId = game.getGameId();
        this.isSpectator = false;

        if (game.getPlayer2() != null) {
            // Game is full, start the game
            startGame(game);
        } else {
            // Player is waiting
            sendMessage("WAITING-Waiting for opponent...");
        }
    }

    private void handleJoinGame(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR-Not authenticated");
            return;
        }

        if (parts.length < 2) {
            sendMessage("ERROR-Invalid join format. Use: join_game-gameId");
            return;
        }

        String gameId = parts[1];
        TicTacToe game = ServerMain.getGamesManager().joinGame(clientUsername, gameId);

        if (game != null) {
            this.currentGameId = gameId;
            this.isSpectator = false;

            if (game.getPlayer2() != null) {
                startGame(game);
            }
        } else {
            sendMessage("ERROR-Could not join game");
        }
    }

    private void handleSpectate(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR-Not authenticated");
            return;
        }

        if (parts.length < 2) {
            sendMessage("ERROR-Invalid spectate format. Use: spectate-gameId");
            return;
        }

        String gameId = parts[1];
        TicTacToe game = ServerMain.getGamesManager().getGame(gameId);

        if (game != null) {
            this.currentGameId = gameId;
            this.isSpectator = true;

            sendMessage("SPECTATE_SUCCESS-Now spectating game " + gameId);

            // Notify other participants in this game session
            String spectatorJoinMsg = "SPECTATOR_JOIN-" + clientUsername + " is now spectating";
            ServerMain.broadcastToGameSession(gameId, spectatorJoinMsg);
        } else {
            sendMessage("ERROR-Game not found");
        }
    }

    private void handleGameMove(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR-Not authenticated");
            return;
        }

        if (currentGameId == null) {
            sendMessage("ERROR-You are not in any game");
            return;
        }

        if (parts.length < 3) {
            sendMessage("ERROR-Invalid move format. Use: move-username-position");
            return;
        }

        String username = parts[1];
        String position = parts[2];

        if (!username.equals(this.clientUsername)) {
            sendMessage("ERROR-Username mismatch");
            return;
        }

        // Find the game this player is in
        TicTacToe game = ServerMain.getGamesManager().getGameByPlayer(username);
        if (game == null || !game.getGameId().equals(currentGameId)) {
            sendMessage("ERROR-Game not found or invalid");
            return;
        }

        // Validate it's player's turn
        if (!game.getTurn().equals(username)) {
            sendMessage("ERROR-Not your turn");
            return;
        }

        // Process the move
        try {
            int pos = Integer.parseInt(position);
            if (makeMove(game, username, pos)) {
                // Broadcast move ONLY to players and spectators in this game
                String moveMsg = "GAME_MOVE-" + game.getGameId() + ":" + username + ":" + position + ":" + game.getTurn();

                ServerMain.broadcastToGameSession(currentGameId, moveMsg);

                // Check if game is over
                if (game.isGameOver()) {
                    handleGameEnd(game);
                }
            } else {
                sendMessage("ERROR-Invalid move");
            }
        } catch (NumberFormatException e) {
            sendMessage("ERROR-Invalid position format");
        }
    }

    private void handleChatMessage(String[] parts) {
        if (!isAuthenticated) {
            sendMessage("ERROR-Not authenticated");
            return;
        }

        if (parts.length < 2) {
            sendMessage("ERROR-Invalid chat format");
            return;
        }

        String chatContent = parts[1];
        String chatMsg = "CHAT-" + clientUsername + ": " + chatContent;

        if (currentGameId != null) {
            // Broadcast chat ONLY to players and spectators in the same game
            ServerMain.broadcastToGameSession(currentGameId, chatMsg);
        } else {
            // Broadcast to lobby if not in a game
            ServerMain.broadcastToAll(chatMsg, this);
        }

        sendMessage("CHAT_ACK-Message sent");
    }

    private void handleLeaveGame() {
        if (currentGameId == null) {
            sendMessage("ERROR-You are not in any game");
            return;
        }

        String gameId = currentGameId;
        TicTacToe game = ServerMain.getGamesManager().getGame(gameId);

        if (isSpectator) {
            // Notify only game session participants
            String spectatorLeaveMsg = "SPECTATOR_LEAVE-" + clientUsername + " stopped spectating";
            ServerMain.broadcastToGameSession(gameId, spectatorLeaveMsg);
        } else if (game != null) {
            // Player leaving the game - notify only game session participants
            String gameEndMsg = "GAME_END-" + gameId + ":FORFEIT:" + clientUsername + " left the game";
            ServerMain.broadcastToGameSession(gameId, gameEndMsg);

            // Remove the game
            ServerMain.getGamesManager().removeGame(gameId);
        }

        this.currentGameId = null;
        this.isSpectator = false;
        sendMessage("LEAVE_SUCCESS-Left the game");
    }

    private void handleLogout() {
        if (isAuthenticated) {
            String logoutMessage = "USER_LEAVE-" + clientUsername + " left the server";
            ServerMain.broadcastToAll(logoutMessage, this);
        }
        cleanup();
    }

    private void startGame(TicTacToe game) {
        String player1 = game.getPlayer1();
        String player2 = game.getPlayer2();
        int player1Elo = EloMod.getCurrentElo(player1);
        int player2Elo = EloMod.getCurrentElo(player2);
        
        // Enhanced game start message with player info and ELO
        // Format: "GAME_START-gameId:player1:player2:dimension:player1Elo:player2Elo"
        String gameStartMsg = "GAME_START-" + game.getGameId() + ":" + 
                            player1 + ":" + player2 + ":" + 
                            game.getDimension() + ":" +
                            player1Elo + ":" + player2Elo;
        
        // Send to both players only
        ServerMain.broadcastToGamePlayers(game.getGameId(), gameStartMsg);
        
        // Remove game from lobby
        String gameRemovedMsg = "GAME_REMOVED-" + game.getGameId();
        ServerMain.broadcastToAll(gameRemovedMsg, null);
        
        System.out.println("Game started: " + game.getGameId() + " between " + player1 + " (" + player1Elo + ") and " + player2 + " (" + player2Elo + ")");
    }

    private void handleGameEnd(TicTacToe game) {
        String winner = game.getWinner();
        String endMsg = "GAME_END-" + game.getGameId() + ":" + (winner != null ? winner : "TIE");

        // Broadcast ONLY to players and spectators in this game
        ServerMain.broadcastToGameSession(currentGameId, endMsg);

        // Update ELO ratings if there's a winner
        if (winner != null) {
            String loser = winner.equals(game.getPlayer1()) ? game.getPlayer2() : game.getPlayer1();
            EloMod.eloUpdate(winner, "win");
            EloMod.eloUpdate(loser, "lose");
        }

        // Remove game
        ServerMain.getGamesManager().removeGame(game.getGameId());

        // Reset client game states for all participants
        for (clientHandler client : ServerMain.getClients()) {
            if (client.getCurrentGameId() != null && client.getCurrentGameId().equals(game.getGameId())) {
                client.currentGameId = null;
                client.isSpectator = false;
            }
        }
    }

    private boolean makeMove(TicTacToe game, String player, int move) {
        int dimension = game.getBoard().length;
        int row = move / dimension;
        int col = move % dimension;

        if (row >= 0 && row < dimension && col >= 0 && col < dimension &&
                game.getBoard()[row][col].equals(" ")) {

            game.getBoard()[row][col] = player.equals(game.getPlayer1()) ? "X" : "O";
            game.setTurn(player.equals(game.getPlayer1()) ? game.getPlayer2() : game.getPlayer1());
            game.checkWinner();
            return true;
        }
        return false;
    }

    // Utility methods
    public boolean isInGame(String gameId) {
        return currentGameId != null && currentGameId.equals(gameId) && !isSpectator;
    }

    public boolean isSpectating(String gameId) {
        return currentGameId != null && currentGameId.equals(gameId) && isSpectator;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private String getClientId() {
        return clientUsername != null ? clientUsername : clientSocket.getRemoteSocketAddress().toString();
    }

    private void cleanup() {
        ServerMain.removeClient(this);

        if (currentGameId != null) {
            handleLeaveGame();
        }

        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Client " + getClientId() + " disconnected. Total clients: " + clients.size());
    }
}