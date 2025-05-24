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
        // No special initialization needed beyond our maps
    }

    /**
     * Create a brand-new game for `player` as player1.
     * - Tears down any existing session for this player.
     * - Sets initial board and state to WAITING_FOR_PLAYER.
     * - Registers the game in all relevant maps.
     */
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

    /**
     * Join an existing waiting game.
     * - Removes any prior session for this `player`.
     * - If `gameId` is valid and slot for player2 is free, assigns player2
     *   and flips state to PLAYER1_TURN.
     * - Removes the original creator from waiting list.
     */
    public synchronized TicTacToe joinGame(String player, String gameId) {
        leaveGame(player);
        TicTacToe game = games.get(gameId);
        if (game == null || game.getPlayer2() != null) {
            return null; // cannot join non-existent or already-full game
        }
        game.setPlayer2(player);
        game.setGameState(GameState.PLAYER1_TURN);
        players.put(player, gameId);
        waitingPlayers.remove(game.getPlayer1());
        return game;
    }

    /**
     * Quick-join logic:
     * - If already in a game, return that session.
     * - Otherwise, pick the first waiting game and join it.
     * - If none are waiting, create a new one.
     */
    public synchronized TicTacToe quickJoin(String player) {
        TicTacToe existing = getGameByPlayer(player);
        if (existing != null) {
            return existing;
        }
        String waitingGameId = waitingPlayers.values().stream().findFirst().orElse(null);
        if (waitingGameId != null) {
            return joinGame(player, waitingGameId);
        }
        return newGame(player);
    }

    /**
     * Add `spectator` to watch the game with `gameId`.
     * Returns true on success, false if no such game exists.
     */
    public synchronized boolean spectateGame(String spectator, String gameId) {
        TicTacToe game = games.get(gameId);
        if (game == null) {
            return false;
        }
        spectators.put(spectator, gameId);
        return true;
    }

    /**
     * Leave any session (waiting, playing, or spectating) for `player`.
     * - If waiting: cancels game completely.
     * - If playing: tears down the game and removes both players.
     * - If spectating: simply removes spectator entry.
     */
    public synchronized void leaveGame(String player) {
        // waiting-to-start case
        if (waitingPlayers.containsKey(player)) {
            String gid = waitingPlayers.remove(player);
            games.remove(gid);
            players.remove(player);
            return;
        }
        // in-progress case
        if (players.containsKey(player)) {
            String gid = players.remove(player);
            TicTacToe game = games.remove(gid);
            if (game != null) {
                String other = game.getPlayer1().equals(player)
                        ? game.getPlayer2()
                        : game.getPlayer1();
                if (other != null) {
                    players.remove(other);
                }
            }
            return;
        }
        // spectating case
        spectators.remove(player);
    }

    /** Retrieve a game by its ID, or null if none exists. */
    public TicTacToe getGame(String gameId) {
        return games.get(gameId);
    }

    /**
     * Find a game where `player` is either player1 or player2.
     * Returns null if not found.
     */
    public TicTacToe getGameByPlayer(String player) {
        return games.values().stream()
                .filter(g -> player.equals(g.getPlayer1()) || player.equals(g.getPlayer2()))
                .findFirst()
                .orElse(null);
    }

    /** Remove a game outright (e.g. cleanup). */
    public void removeGame(String gameId) {
        games.remove(gameId);
    }

    /** Expose internal maps for introspection or administration. */
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
