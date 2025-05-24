package com.bumba.tic_tac_toe.server;

import com.bumba.tic_tac_toe.ServerMain;
import com.bumba.tic_tac_toe.game.TicTacToe;

/**
 * Parses a raw command string from a client and dispatches to GamesManager
 * Builds a standardized response to send back.
 */
public class SessionController {

    private final GamesManager gm;
    private final String command;
    private String result;

    /**
     * Constructor grabs the singleton GamesManager from ServerMain
     * and immediately processes the incoming command.
     */
    public SessionController(String command) {
        this.gm = ServerMain.getGamesManager();
        this.command = command;
        processCommand();
    }

    /**
     * Splits the command by '-' and routes to the correct GamesManager method.
     * Supported tags:
     *   create_game, join_game, quick_join, leave_game, spectate
     * Populates `result` with a response string.
     */
    private void processCommand() {
        String[] parts = command.split("-");
        String tag = parts[0];

        switch (tag) {
            case "create_game":
                // Expect: create_game-username
                if (parts.length < 2) {
                    result = "ERROR-Invalid format. Use create_game-username";
                    break;
                }
                TicTacToe newGame = gm.newGame(parts[1]);
                result = "GAME_CREATED-" + newGame.getGameId();
                break;

            case "join_game":
                // Expect: join_game-username-gameId
                if (parts.length < 3) {
                    result = "ERROR-Invalid. Use join_game-username-gameId";
                    break;
                }
                TicTacToe joined = gm.joinGame(parts[1], parts[2]);
                result = (joined != null)
                        ? "GAME_JOINED-" + parts[2]
                        : "ERROR-Could not join game " + parts[2];
                break;

            case "quick_join":
                // Expect: quick_join-username
                if (parts.length < 2) {
                    result = "ERROR-Invalid. Use quick_join-username";
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
                    result = "ERROR-Invalid. Use leave_game-username";
                    break;
                }
                gm.leaveGame(parts[1]);
                result = "GAME_LEFT-" + parts[1];
                break;

            case "spectate":
                // Expect: spectate-username-gameId
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
        }
    }

    /** Returns the response string to send back to the client. */
    public String getResult() {
        return result;
    }
}
