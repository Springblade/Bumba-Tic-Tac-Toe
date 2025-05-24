package com.bumba.tic_tac_toe.server;

import com.bumba.tic_tac_toe.enumeration.GameState;
import com.bumba.tic_tac_toe.game.TicTacToe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamesManager {

    private final Map<String, TicTacToe> games = new ConcurrentHashMap<>();
    private final Map<String, String> waitingPlayers = new ConcurrentHashMap<>();
    private final Map<String, String> players = new ConcurrentHashMap<>();
    private final Map<String, String> spectators = new ConcurrentHashMap<>();

    public GamesManager() {
        // nothing else to init
    }

    /** Create a new game and put player1 into waiting state */
    public synchronized TicTacToe newGame(String player) {
        leaveGame(player);
        TicTacToe game = new TicTacToe();
        String gameId = game.getGameId();
        game.setPlayer1(player);
        game.setGameState(GameState.WAITING_FOR_PLAYER);
        game.setBoard();
        games.put(gameId, game);
        waitingPlayers.put(player, gameId);
        players.put(player, gameId);
        return game;
    }

    /** Join a specific waiting game */
    public synchronized TicTacToe joinGame(String player, String gameId) {
        leaveGame(player);
        TicTacToe game = games.get(gameId);
        if (game == null || game.getPlayer2() != null) return null;
        game.setPlayer2(player);
        game.setGameState(GameState.PLAYER1_TURN);
        players.put(player, gameId);
        waitingPlayers.remove(game.getPlayer1());
        return game;
    }

    /** Either return an existing session, join the first waiting game, or create one */
    public synchronized TicTacToe quickJoin(String player) {
        TicTacToe existing = getGameByPlayer(player);
        if (existing != null) return existing;

        // pick any waiting game
        String waitingGameId = waitingPlayers.values().stream().findFirst().orElse(null);
        if (waitingGameId != null) {
            return joinGame(player, waitingGameId);
        }

        return newGame(player);
    }

    /** Add a spectator to a game */
    public synchronized boolean spectateGame(String spectator, String gameId) {
        TicTacToe game = games.get(gameId);
        if (game == null) return false;
        spectators.put(spectator, gameId);
        return true;
    }

    /** Remove a player/spectator from any session, tearing down games as needed */
    public synchronized void leaveGame(String player) {
        // If waiting to start
        if (waitingPlayers.containsKey(player)) {
            String gid = waitingPlayers.remove(player);
            games.remove(gid);
            players.remove(player);
            return;
        }
        // If playing
        if (players.containsKey(player)) {
            String gid = players.remove(player);
            TicTacToe game = games.remove(gid);
            if (game != null) {
                String other = game.getPlayer1().equals(player) ? game.getPlayer2() : game.getPlayer1();
                if (other != null) players.remove(other);
            }
            return;
        }
        // If spectating
        spectators.remove(player);
    }

    public TicTacToe getGame(String gameId) {
        return games.get(gameId);
    }

    public TicTacToe getGameByPlayer(String player) {
        return games.values().stream()
                .filter(g -> player.equals(g.getPlayer1()) || player.equals(g.getPlayer2()))
                .findFirst().orElse(null);
    }

    public void removeGame(String gameId) {
        games.remove(gameId);
    }

    public Map<String, TicTacToe> getGames() {
        return games;
    }

    public Map<String, String> getWaitingPlayers() {
        return waitingPlayers;
    }

    public Map<String, String> getPlayers() {
        return players;
    }

    public Map<String, String> getSpectators() {
        return spectators;
    }
}
