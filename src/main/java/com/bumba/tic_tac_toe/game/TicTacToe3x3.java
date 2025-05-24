package com.bumba.tic_tac_toe.game;

import java.util.Objects;

class TicTacToe3x3 extends TicTacToe {

    TicTacToe3x3(String player1, String player2) {
        super(player1, player2);
        setDimension(3);
        initBoard();
    }

    @Override
    public void checkWinner() {
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
