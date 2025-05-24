package com.bumba.tic_tac_toe.server;

import com.bumba.tic_tac_toe.game.TicTacToe;
import com.bumba.tic_tac_toe.ServerMain;


class SessionController {
    private final GamesManager gm;
    private final String command;
    private String result;

    public SessionController(String command) {
        // Use the singleton manager from ServerMain
        this.gm = ServerMain.getGamesManager();
        this.command = command;
        processCommand();
    }

    private void processCommand() {
        String[] parts = command.split("-");
        String tag = parts[0];

        switch (tag) {
            case "create_game":
                if (parts.length < 2) {
                    result = "ERROR-Invalid format. Use create_game-username";
                    break;
                }
                TicTacToe newGame = gm.newGame(parts[1]);
                result = "GAME_CREATED-" + newGame.getGameId();
                break;

            case "join_game":
                if (parts.length < 3) {
                    result = "ERROR-Invalid format. Use join_game-username-gameId";
                    break;
                }
                TicTacToe joined = gm.joinGame(parts[1], parts[2]);
                result = (joined != null)
                        ? "GAME_JOINED-" + parts[2]
                        : "ERROR-Could not join game " + parts[2];
                break;

            case "quick_join":
                if (parts.length < 2) {
                    result = "ERROR-Invalid format. Use quick_join-username";
                    break;
                }
                TicTacToe qj = gm.quickJoin(parts[1]);
                if (qj.getPlayer2() != null) {
                    result = "GAME_STARTED-" + qj.getGameId();
                } else {
                    result = "WAITING_FOR_OPPONENT-" + qj.getGameId();
                }
                break;

            case "leave_game":
                if (parts.length < 2) {
                    result = "ERROR-Invalid format. Use leave_game-username";
                    break;
                }
                gm.leaveGame(parts[1]);
                result = "GAME_LEFT-" + parts[1];
                break;

            case "spectate":
                if (parts.length < 3) {
                    result = "ERROR-Use spectate-username-gameId";
                    break;
                }
                boolean ok = gm.spectateGame(parts[1], parts[2]);
                result = ok
                        ? "SPECTATING-" + parts[2]
                        : "ERROR-Game not found: " + parts[2];
                break;

            default:
                result = "ERROR-Unknown command: " + tag;
                break;
        }
    }

    /** After construction, call this to get the response you should send/broadcast. */
    public String getResult() {
        return result;
    }
}
