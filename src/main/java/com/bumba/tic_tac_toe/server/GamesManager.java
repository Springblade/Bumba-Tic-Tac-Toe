package com.bumba.tic_tac_toe.server;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bumba.tic_tac_toe.enumeration.GameState;
import com.bumba.tic_tac_toe.game.TicTacToe;

public class GamesManager {

    private final Map<String, TicTacToe> games = new ConcurrentHashMap<>();
    private final Map<String, String> waitingPlayers = new ConcurrentHashMap<>();
    private final Map<String, String> players = new ConcurrentHashMap<>();
    private final Map<String, String> spectators = new ConcurrentHashMap<>();


    public synchronized TicTacToe newGame(String player) {
        leaveGame(player);
        TicTacToe game = new TicTacToe(player,null);
        String gameId = game.getGameId();
        game.setGameState(GameState.WAITING_FOR_PLAYER);
        game.setBoard();
        games.put(gameId, game);
        waitingPlayers.put(player, gameId);
        players.put(player, gameId);
        return game;
    }


    public synchronized TicTacToe joinGame(String player, String gameId) {
        leaveGame(player);
        TicTacToe game = games.get(gameId);
        if (game == null || game.getPlayer2() != null) {
            return null;
        }
        game.setPlayer2(player);
        game.setGameState(GameState.PLAYER1_TURN);
        players.put(player, gameId);
        waitingPlayers.remove(game.getPlayer1());
        return game;
    }


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


    public synchronized boolean spectateGame(String spectator, String gameId) {
        TicTacToe game = games.get(gameId);
        if (game == null) {
            return false;
        }
        spectators.put(spectator, gameId);
        return true;
    }


    public synchronized void leaveGame(String player) {
        if (waitingPlayers.containsKey(player)) {
            String gid = waitingPlayers.remove(player);
            games.remove(gid);
            players.remove(player);
            return;
        }
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
        spectators.remove(player);
    }

    //Retrieve a game by its ID, or null if none exists.
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

    public Collection<TicTacToe> getAllGames() {
        return games.values();
    }
}