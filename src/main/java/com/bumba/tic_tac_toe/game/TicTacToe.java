package com.bumba.tic_tac_toe.game;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.bumba.tic_tac_toe.enumeration.GameState;

public class TicTacToe {
    private String gameId;
    private String player1;
    private String player2;
    private List<String> spectators;
    private String winner;
    private String turn;
    private int dimension;
    private GameState gameState;
    public String[][] board;

    public TicTacToe(String player1, String player2) {
        this.gameId = UUID.randomUUID().toString().replace("-", "");
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

    public boolean makeMove(int position, String player) {
        // Convert position to row/col
        int row = position / dimension;
        int col = position % dimension;
        
        // Check if position is valid and empty
        if (row < 0 || row >= dimension || col < 0 || col >= dimension || 
            !board[row][col].equals(" ")) {
            return false;
        }
        
        // Make the move
        board[row][col] = player.equals(player1) ? "X" : "O";
        
        // Switch turn
        turn = player.equals(player1) ? player2 : player1;
        
        // Check for winner
        checkWinner();
        
        // Update game state
        updateGameState();
        
        // Debug output
        System.out.println("Move made at position " + position + " by " + player);
        printBoard();
        
        return true;
    }

    public void checkWinner() {
        // Check rows
        for (int i = 0; i < dimension; i++) {
            if (!board[i][0].equals(" ") && 
                board[i][0].equals(board[i][1]) && 
                board[i][0].equals(board[i][2])) {
                winner = board[i][0].equals("X") ? player1 : player2;
                return;
            }
        }
        
        // Check columns
        for (int i = 0; i < dimension; i++) {
            if (!board[0][i].equals(" ") && 
                board[0][i].equals(board[1][i]) && 
                board[0][i].equals(board[2][i])) {
                winner = board[0][i].equals("X") ? player1 : player2;
                return;
            }
        }
        
        // Check diagonals
        if (!board[0][0].equals(" ") && 
            board[0][0].equals(board[1][1]) && 
            board[0][0].equals(board[2][2])) {
            winner = board[0][0].equals("X") ? player1 : player2;
            return;
        }
        
        if (!board[0][2].equals(" ") && 
            board[0][2].equals(board[1][1]) && 
            board[0][2].equals(board[2][0])) {
            winner = board[0][2].equals("X") ? player1 : player2;
            return;
        }
    }

    private void updateGameState() {
        if (winner != null) {
            gameState = winner.equals(player1) ? gameState.PLAYER1_WON : gameState.PLAYER2_WON;
        } else if (isBoardFull()) {
            gameState = gameState.TIE;
        } else {
            gameState = turn.equals(player1) ? gameState.PLAYER1_TURN : gameState.PLAYER2_TURN;
        }
    }

    public boolean isGameOver() {
        // Game is over if there's a winner or the board is full (tie)
        return winner != null || isBoardFull();
    }

    public boolean isBoardFull() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j].equals(" ")) {
                    return false;
                }
            }
        }
        return true;
    }

    // Debug method to print the current board state
    public void printBoard() {
        System.out.println("Current board state for game " + gameId + ":");
        for (int i = 0; i < dimension; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < dimension; j++) {
                row.append("[").append(board[i][j]).append("]");
            }
            System.out.println(row.toString());
        }
        System.out.println("Current turn: " + turn);
        System.out.println("Game state: " + gameState);
        System.out.println("Winner: " + (winner != null ? winner : "none"));
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
        initBoard();
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

    public int getDimension() {
        return dimension;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

}

