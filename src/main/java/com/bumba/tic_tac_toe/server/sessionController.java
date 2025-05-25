package com.bumba.tic_tac_toe.server;

import java.util.Collection;

import com.bumba.tic_tac_toe.ServerMain;
import com.bumba.tic_tac_toe.game.TicTacToe;


class SessionController {

    private final GamesManager gm;
    private final String command;
    private String result;


    public SessionController(String command) {
        this.gm = ServerMain.getGamesManager();
        this.command = command;
        processCommand();
    }


    private void processCommand() {
        String[] parts = command.split("-");
        String tag = parts[0];

        switch (tag) {
            case "create_game":
                // Expected: create_game-username
                if (parts.length < 2) {
                    result = "ERROR-31";
                    break;
                }
                TicTacToe newGame = gm.newGame(parts[1]);
                result = "GAME_CREATED-" + newGame.getGameId();
                break;

            case "join_game":
                // Expected: join_game-username-gameId
                if (parts.length < 3) {
                    result = "ERROR-32";
                    break;
                }
                TicTacToe joined = gm.joinGame(parts[1], parts[2]);
                result = (joined != null)
                        ? "GAME_JOINED-" + parts[2]
                        : "ERROR-Could not join game " + parts[2];
                break;

            case "quick_join":
                // Expected: quick_join-username
                if (parts.length < 2) {
                    result = "ERROR-33";
                    break;
                }
                TicTacToe qj = gm.quickJoin(parts[1]);
                // If slot for player2 is now filled, game starts; otherwise wait.
                result = (qj.getPlayer2() != null)
                        ? "GAME_STARTED-" + qj.getGameId()
                        : "WAITING_FOR_OPPONENT-" + qj.getGameId();
                break;

            case "leave_game":
                // Expect: leave_game-username
                if (parts.length < 2) {
                    result = "ERROR-34";
                    break;
                }
                gm.leaveGame(parts[1]);
                result = "GAME_LEFT-" + parts[1];
                break;

            case "spectate":
                // Expect: spectate-username-gameId
                if (parts.length < 3) {
                    result = "ERROR-35";
                    break;
                }
                boolean ok = gm.spectateGame(parts[1], parts[2]);
                result = ok
                        ? "SPECTATING-" + parts[2]
                        : "ERROR-Game not found: " + parts[2];
                break;
            case "list_games":
                Collection<TicTacToe> allGames = gm.getAllGames();
                if (allGames.isEmpty()) {
                    result = "NO_GAMES";
                } else {
                    StringBuilder sb = new StringBuilder("GAMES_LIST");
                    for (TicTacToe game : allGames) {
                        sb.append("-").append(game.getGameId());
                    }
                    result = sb.toString();
                }
                break;
            default:
                result = "ERROR-Unknown command: " + tag;
        }
    }

    public String getResult() {
        return result;
    }
}