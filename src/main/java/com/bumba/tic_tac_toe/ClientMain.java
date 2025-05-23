package com.bumba.tic_tac_toe;

import java.io.IOException;

import com.bumba.tic_tac_toe.client.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

public class ClientMain extends Application {

    private Client client;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("logInScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setResizable(false);
        stage.setTitle("Tic Tac Toe");
        stage.setScene(scene);
        stage.show();

    }

    public void connect(){
        try {
            this.client = new Client("localhost", 5000, this::onMessageReceived);
            client.startClient();
        } catch (IOException e) {}
    }

    public void sendMessage(String msg) {
        // static call error
        client.sendMessage(msg);
    }

    private void onMessageReceived(String message) {
//         Platform.runLater(() -> messageArea.appendText(message + "\n"));
    }

    public static void main(String[] args) {launch();}
}

