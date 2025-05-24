package com.bumba.tic_tac_toe.Controller;

import com.bumba.tic_tac_toe.ClientMain;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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

    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private Stage stage;

    private ClientMain client;

    public static class GameEntry {
        String ID, status;
        GameEntry(String ID, String status) { this.ID = ID; this.status = status; }
    }

    @FXML
    public void initialize() {
        ObservableList<GameEntry> games = FXCollections.observableArrayList();
        if(client.getGameList() != null) {
            for (String game : client.getGameList()) {
                String[] parts = game.split(",");
                GameEntry newGame = new GameEntry(parts[0], parts[1]);
                games.add(newGame);
            }
        }


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

    private void refresh() {
        gameListView.getItems().clear();
        ObservableList<GameEntry> games = FXCollections.observableArrayList();
        if(client.getGameList() != null) {
            for (String game : client.getGameList()) {
                String[] parts = game.split(",");
                GameEntry newGame = new GameEntry(parts[0], parts[1]);
                games.add(newGame);
            }
        }
        gameListView.setItems(games);
    }

    @FXML
    protected void createGame() {
        //create game session
    }

    protected void joinGame() {
        //send msg to server
    }

    protected void spectate() {
        //send msg to server
    }

    protected void quickJoin() {
        //send msg to server
    }


    @FXML
    private void transitionToGame() {
        try {
            root = FXMLLoader.load(getClass().getResource("/com/bumba/tic_tac_toe/game.fxml"));
            scene = new Scene(root);
            stage = (Stage) createGameButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
