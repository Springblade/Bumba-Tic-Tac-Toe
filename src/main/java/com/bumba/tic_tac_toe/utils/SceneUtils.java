package com.bumba.tic_tac_toe.utils;

import com.bumba.tic_tac_toe.client.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneUtils {

    public static void switchScene(String fxmlPath, ActionEvent event, Client client) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
        Parent root = loader.load();

        Object controller = loader.getController();

        if (controller instanceof MessageListener && controller instanceof ClientAwareController) {
            ((ClientAwareController) controller).setClient(client);
            client.setMessageListener((MessageListener) controller);
        }

        // Get the current stage and switch scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }
}

