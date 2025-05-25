package com.bumba.tic_tac_toe;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthenController {
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Button login;
    @FXML private Button signup;
    @FXML private Label errorLabel;

    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private Stage stage;

    private ClientMain clientMain;

    @FXML
    private void LogIn(ActionEvent event) {
        clientMain.login(username.getText(),password.getText());
    }

    @FXML
    private void SignUp(ActionEvent event) {
        clientMain.register(username.getText(),password.getText());
    }

    public void handleError(String errorMessage) {
        errorLabel.setText(errorMessage);
    }

    @FXML
    public void transitionToLobbyScene() {
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