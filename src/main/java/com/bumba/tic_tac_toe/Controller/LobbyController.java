package com.bumba.tic_tac_toe.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class LobbyController {

    @FXML private ListView<GameEntry> gameListView;
    @FXML private Button createGameButton;
    @FXML private Button quickJoinButton;
    @FXML private Button spectateButton;
    @FXML private Button refreshButton;
    @FXML private Button joinGameButton;
    @FXML private Label IDLabel;
    @FXML private Label statusLabel;

    public static class GameEntry {
        String ID, status;
        GameEntry(String ID, String status) { this.ID = ID; this.status = status; }
    }

    @FXML
    public void initialize() {
        //get list of all ongoing games
        //sendMsg(
        ObservableList<GameEntry> games = FXCollections.observableArrayList(
        );

        gameListView.setItems(games);
        gameListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(GameEntry newGame, boolean empty) {
                super.updateItem(newGame, empty);
                if (empty || newGame == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("server_item.fxml"));
                        HBox hbox = loader.load();

                        IDLabel.setText(newGame.ID);
                        statusLabel.setText(newGame.status);

                        joinGameButton.setOnAction(event -> System.out.println("Connecting to " + newGame.ID));
                        quickJoinButton.setOnAction(event -> System.out.println("Connecting to " + newGame.ID));
                        spectateButton.setOnAction(event -> System.out.println("Showing info for " + newGame.ID));


                        setGraphic(hbox);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}
