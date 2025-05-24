module com.bumba.tic_tac_toe {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.bumba.tic_tac_toe to javafx.fxml;
    exports com.bumba.tic_tac_toe;
    exports com.bumba.tic_tac_toe.client;
    opens com.bumba.tic_tac_toe.client to javafx.fxml;
    exports com.bumba.tic_tac_toe.Controller;
    opens com.bumba.tic_tac_toe.Controller to javafx.fxml;
    exports com.bumba.tic_tac_toe.server;
    opens com.bumba.tic_tac_toe.server to javafx.fxml;
    exports com.bumba.tic_tac_toe.game;
    opens com.bumba.tic_tac_toe.game to javafx.fxml;
    exports com.bumba.tic_tac_toe.enumeration;
    opens com.bumba.tic_tac_toe.enumeration to javafx.fxml;
    exports com.bumba.tic_tac_toe.database;
    opens com.bumba.tic_tac_toe.database to javafx.fxml;
}