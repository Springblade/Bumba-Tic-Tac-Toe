package com.bumba.tic_tac_toe.server;

import com.bumba.tic_tac_toe.game.GamesManager;

public class sessionController {
    private GamesManager gm;
    private String command;

    public sessionController(String msg) {
        gm = new GamesManager();
        command = msg;
    }

    public void processMsg(){
        String[] input = command.split("");
        switch(input[0]){
            case "connect":
                //send back authen request
            case "register":
                //get the next 2 line
            case "join":
                //1 msg with ID to join the game
            case "quickjoin":
                //
            case "spec":
            case "rank":
        }

    }

    public void getResult(){

    }
}


