package com.bumba.tic_tac_toe.Controller;

import com.bumba.tic_tac_toe.ClientMain;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthenController {
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Button login;
    @FXML private Button signup;

    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private Stage stage;

    private ClientMain clientMain;

    @FXML
    private void LogIn(ActionEvent event) {
        clientMain.sendMessage("LogIn");
        clientMain.sendMessage(username.getText());
        clientMain.sendMessage(password.getText());
    }

    @FXML
    private void SignUp(ActionEvent event) {
        clientMain.sendMessage("SignUp");
        clientMain.sendMessage(username.getText());
        clientMain.sendMessage(password.getText());
    }

    public void handleServerResponse(String response) {
        //get the response from the server
    }

    @FXML
    private void transitionToLobbyScene() {
        try {
            root = FXMLLoader.load(getClass().getResource("/com/bumba/tic_tac_toe/lobbyScene.fxml"));
            scene = new Scene(root);
            stage = (Stage) login.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}