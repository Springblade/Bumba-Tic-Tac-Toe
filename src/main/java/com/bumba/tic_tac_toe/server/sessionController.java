package com.bumba.tic_tac_toe.server;

import com.bumba.tic_tac_toe.game.TicTacToe;

public class sessionController {
    private GamesManager gm;
    private String command;

    public sessionController(String msg) {
        gm = new GamesManager();
        command = msg;
    }



    public void getResult(){

    }
}


