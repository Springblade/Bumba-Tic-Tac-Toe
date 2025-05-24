package com.bumba.tic_tac_toe.server;

import com.bumba.tic_tac_toe.enumeration.GameState;
import com.bumba.tic_tac_toe.game.TicTacToe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamesManager {

    private final Map<String, TicTacToe> games;
    protected final Map<String, String> waitingPlayers;
    protected final Map<String, String> players;
    protected final Map<String, String> spectators;

    public GamesManager() {
        games = new ConcurrentHashMap<>();
        waitingPlayers = new ConcurrentHashMap<>();
        players = new ConcurrentHashMap<>();
        spectators = new ConcurrentHashMap<>();
    }

    public synchronized TicTacToe newGame(String player) {
        TicTacToe TicTacToe = new TicTacToe(player, null);
        games.put(TicTacToe.getGameId(), TicTacToe);
        waitingPlayers.put(player, TicTacToe.getGameId());
        return TicTacToe;
    }

    public synchronized TicTacToe joinGame(String player,String gameId) {
        TicTacToe TicTacToe = games.get(gameId);
        TicTacToe.setPlayer2(player);
        TicTacToe.setGameState(GameState.PLAYER1_TURN);
        return TicTacToe;
    }

    public synchronized TicTacToe quickJoin(String player) {
        if (games.values().stream().anyMatch(TicTacToe -> TicTacToe.getPlayer1().equals(player) || (TicTacToe.getPlayer2() != null && TicTacToe.getPlayer2().equals(player)))) {
            return games.values().stream().filter(TicTacToe -> TicTacToe.getPlayer1().equals(player) || TicTacToe.getPlayer2().equals(player)).findFirst().get();
        }

        for (TicTacToe TicTacToe : games.values()) {
            if (TicTacToe.getPlayer1() != null && TicTacToe.getPlayer2() == null) {
                TicTacToe.setPlayer2(player);
                TicTacToe.setGameState(GameState.PLAYER1_TURN);
                return TicTacToe;
            }
        }

        TicTacToe TicTacToe = new TicTacToe(player, null);
        games.put(TicTacToe.getGameId(), TicTacToe);
        waitingPlayers.put(player, TicTacToe.getGameId());
        return TicTacToe;
    }

    public synchronized TicTacToe leaveGame(String player) {
        String gameId = getGameByPlayer(player) != null ? getGameByPlayer(player).getGameId() : null;
        if (gameId != null) {
            waitingPlayers.remove(player);
            TicTacToe game = games.get(gameId);
            if (player.equals(game.getPlayer1())) {
                if (game.getPlayer2() != null) {
                    game.setPlayer1(game.getPlayer2());
                    game.setPlayer2(null);
                    game.setGameState(GameState.WAITING_FOR_PLAYER);
                    game.setBoard();
                    waitingPlayers.put(game.getPlayer1(), game.getGameId());
                } else {
                    games.remove(gameId);
                    if (games.isEmpty()) {
                        //shutdown
                    }
                    return null;
                }
            } else if (player.equals(game.getPlayer2())) {
                game.setPlayer2(null);
                game.setGameState(GameState.WAITING_FOR_PLAYER);
                game.setBoard();
                waitingPlayers.put(game.getPlayer1(), game.getGameId());
            }
            return game;
        }
        return null;
    }

    public TicTacToe getGame(String gameId) {
        return games.get(gameId);
    }

    public TicTacToe getGameByPlayer(String player) {
        return games.values().stream().filter(game -> game.getPlayer1().equals(player) || (game.getPlayer2() != null &&
                game.getPlayer2().equals(player))).findFirst().orElse(null);
    }

    public void removeGame(String gameId) {
        games.remove(gameId);
    }

    public Map<String, TicTacToe> getGames() {
        return games;
    }
}
