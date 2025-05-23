package com.bumba.tic_tac_toe.Controller;

import com.bumba.tic_tac_toe.ClientMain;
import com.bumba.tic_tac_toe.utils.SceneUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import com.bumba.tic_tac_toe.client.Client;
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



    private void LogIn(ActionEvent event) {
        ClientMain.sendMessage("LogIn");
        ClientMain.sendMessage(username.getText());
        ClientMain.sendMessage(password.getText());
    }


    private void SignUp(ActionEvent event) {
        ClientMain.sendMessage("SignUp");
        ClientMain.sendMessage(username.getText());
        ClientMain.sendMessage(password.getText());
    }

    private void handleAuthen(String message) {
        if (message.equals("Success")||message.equals("Created")) {
            //to next scene
        } else if (message.startsWith("")) {
            // Show error message
        }
    }




}