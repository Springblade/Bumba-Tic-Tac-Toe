// package com.bumba.tic_tac_toe.game;

// import java.util.Objects;

// class TicTacToe9x9 extends TicTacToe {

//     TicTacToe9x9(String player1, String player2) {
//         super(player1, player2);
//         setDimension(9);
//         initBoard();
//     }

//     @Override
//     public void checkWinner() {

//         for (int i = 0; i < 9; i++) {
//             for (int j = 0; j <= 4; j++) {
//                 if (Objects.equals(board[i][j], board[i][j + 1]) &&
//                         Objects.equals(board[i][j], board[i][j + 2]) &&
//                         Objects.equals(board[i][j], board[i][j + 3]) &&
//                         Objects.equals(board[i][j], board[i][j + 4]) &&
//                         !Objects.equals(board[i][j], " ")) {
//                     setWinner(Objects.equals(board[i][j], getPlayer1()) ? getPlayer1() : getPlayer2());
//                     return;
//                 }
//             }
//         }

//         for (int i = 0; i < 9; i++) {
//             for (int j = 0; j <= 4; j++) {
//                 if (Objects.equals(board[j][i], board[j + 1][i]) &&
//                         Objects.equals(board[j][i], board[j + 2][i]) &&
//                         Objects.equals(board[j][i], board[j + 3][i]) &&
//                         Objects.equals(board[j][i], board[j + 4][i]) &&
//                         !Objects.equals(board[j][i], " ")) {
//                     setWinner(Objects.equals(board[j][i], getPlayer1()) ? getPlayer1() : getPlayer2());
//                     return;
//                 }
//             }
//         }


//         for (int i = 0; i <= 4; i++) {
//             for (int j = 0; j <= 4; j++) {
//                 if (Objects.equals(board[i][j], board[i + 1][j + 1]) &&
//                         Objects.equals(board[i][j], board[i + 2][j + 2]) &&
//                         Objects.equals(board[i][j], board[i + 3][j + 3]) &&
//                         Objects.equals(board[i][j], board[i + 4][j + 4]) &&
//                         !Objects.equals(board[i][j], " ")) {
//                     setWinner(Objects.equals(board[i][j], getPlayer1()) ? getPlayer1() : getPlayer2());
//                     return;
//                 }
//             }
//         }


//         for (int i = 0; i <= 4; i++) {
//             for (int j = 4; j < 9; j++) {
//                 if (Objects.equals(board[i][j], board[i + 1][j - 1]) &&
//                         Objects.equals(board[i][j], board[i + 2][j - 2]) &&
//                         Objects.equals(board[i][j], board[i + 3][j - 3]) &&
//                         Objects.equals(board[i][j], board[i + 4][j - 4]) &&
//                         !Objects.equals(board[i][j], " ")) {
//                     setWinner(Objects.equals(board[i][j], getPlayer1()) ? getPlayer1() : getPlayer2());
//                     return;
//                 }
//             }
//         }
//     }

// }
