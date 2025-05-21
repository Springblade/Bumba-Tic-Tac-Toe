package Tic_Tac_Toe.game;

import Tic_Tac_Toe.enumeration.gameState;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TicTacToe {
    private String gameId;
    private String player1;
    private String player2;
    private List<String> spectators;
    private String winner;
    private String turn;
    private int dimension;
    private gameState gameState;
    public String[][] board;

    public TicTacToe(String player1, String player2) {
        this.gameId = UUID.randomUUID().toString();
        this.player1 = player1;
        this.player2 = player2;
        this.turn = player1;
        gameState = gameState.WAITING_FOR_PLAYER;
    }

    public void initBoard() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                this.board[i][j] = " ";
            }
        }
    }

    private void makeMove(String player, int move) {
        int row = move / dimension;
        int col = move % dimension;
        if (Objects.equals(board[row][col], " ")) {
            board[row][col] = Objects.equals(player, player1) ? "X" : "O";
            turn = player.equals(player1) ? player2 : player1;
            checkWinner();
            updateGameState();
        }
    }

    public void checkWinner() {}

    private void updateGameState() {
        if (winner != null) {
            gameState = winner.equals(player1) ? gameState.PLAYER1_WON : gameState.PLAYER2_WON;
        } else if (isBoardFull()) {
            gameState = gameState.TIE;
        } else {
            gameState = turn.equals(player1) ? gameState.PLAYER1_TURN : gameState.PLAYER2_TURN;
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (Objects.equals(board[i][j], " ")) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isGameOver() {
        return winner != null || isBoardFull();
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard() {
        this.board = new String[dimension][dimension];
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public void addSpectator(String spectator) {
        this.spectators.add(spectator);
    }

    public List<String> getSpectator() {
        return spectators;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public gameState getGameState() {
        return gameState;
    }

    public void setGameState(gameState gameState) {
        this.gameState = gameState;
    }

}

class TicTacToe3x3 extends TicTacToe{

    TicTacToe3x3(String player1, String player2) {
        super(player1, player2);
        setDimension(3);
        initBoard();
    }

    @Override
    public void checkWinner(){
        for (int i = 0; i < 3; i++) {
            if (Objects.equals(board[i][0], board[i][1]) && Objects.equals(board[i][0], board[i][2])) {
                if (!Objects.equals(board[i][0], " ")) {
                    setWinner(Objects.equals(board[i][0], getPlayer1()) ? getPlayer1() : getPlayer2());
                    return;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (Objects.equals(board[0][i], board[1][i]) && Objects.equals(board[0][i], board[2][i])) {
                if (!Objects.equals(board[0][i], " ")) {
                    setWinner(Objects.equals(board[0][i], getPlayer1()) ? getPlayer1() : getPlayer2());
                    return;
                }
            }
        }

        if (Objects.equals(board[0][0], board[1][1]) && Objects.equals(board[0][0], board[2][2])) {
            if (!Objects.equals(board[0][0], " ")) {
                setWinner(Objects.equals(board[0][0], getPlayer1()) ? getPlayer1() : getPlayer2());
                return;
            }
        }
    }
}

class TicTacToe9x9 extends TicTacToe{

    TicTacToe9x9(String player1, String player2) {
        super(player1, player2);
        setDimension(9);
        initBoard();
    }

    @Override
    public void checkWinner() {

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j <= 4; j++) {
                if (Objects.equals(board[i][j], board[i][j + 1]) &&
                        Objects.equals(board[i][j], board[i][j + 2]) &&
                        Objects.equals(board[i][j], board[i][j + 3]) &&
                        Objects.equals(board[i][j], board[i][j + 4]) &&
                        !Objects.equals(board[i][j], " ")) {
                    setWinner(Objects.equals(board[i][j], getPlayer1()) ? getPlayer1() : getPlayer2());
                    return;
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j <= 4; j++) {
                if (Objects.equals(board[j][i], board[j + 1][i]) &&
                        Objects.equals(board[j][i], board[j + 2][i]) &&
                        Objects.equals(board[j][i], board[j + 3][i]) &&
                        Objects.equals(board[j][i], board[j + 4][i]) &&
                        !Objects.equals(board[j][i], " ")) {
                    setWinner(Objects.equals(board[j][i], getPlayer1()) ? getPlayer1() : getPlayer2());
                    return;
                }
            }
        }


        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
                if (Objects.equals(board[i][j], board[i + 1][j + 1]) &&
                        Objects.equals(board[i][j], board[i + 2][j + 2]) &&
                        Objects.equals(board[i][j], board[i + 3][j + 3]) &&
                        Objects.equals(board[i][j], board[i + 4][j + 4]) &&
                        !Objects.equals(board[i][j], " ")) {
                    setWinner(Objects.equals(board[i][j], getPlayer1()) ? getPlayer1() : getPlayer2());
                    return;
                }
            }
        }


        for (int i = 0; i <= 4; i++) {
            for (int j = 4; j < 9; j++) {
                if (Objects.equals(board[i][j], board[i + 1][j - 1]) &&
                        Objects.equals(board[i][j], board[i + 2][j - 2]) &&
                        Objects.equals(board[i][j], board[i + 3][j - 3]) &&
                        Objects.equals(board[i][j], board[i + 4][j - 4]) &&
                        !Objects.equals(board[i][j], " ")) {
                    setWinner(Objects.equals(board[i][j], getPlayer1()) ? getPlayer1() : getPlayer2());
                    return;
                }
            }
        }
    }

}
