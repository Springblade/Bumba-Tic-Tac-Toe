package com.bumba.tic_tac_toe.Controller;

import com.bumba.tic_tac_toe.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class GameController {
    @FXML private GridPane board;

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    protected void onClick() {

    }

    private int handleButtonPress(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        int row = GridPane.getRowIndex(clickedButton) == null ? 0 : GridPane.getRowIndex(clickedButton);
        int col = GridPane.getColumnIndex(clickedButton) == null ? 0 : GridPane.getColumnIndex(clickedButton);

        int move = row * 3 + col;
        return move;
    }


}
