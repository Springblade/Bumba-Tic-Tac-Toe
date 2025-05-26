package com.bumba.tic_tac_toe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class GameController {
    @FXML private GridPane board3x3;
    @FXML private GridPane board9x9;
    @FXML private Button ffButton;
    @FXML private Button sendButton;
    @FXML private Label playername1;
    @FXML private Label playername2;
    @FXML private Label elo1;
    @FXML private Label elo2;
    @FXML private ListView<String> chatArea;
    @FXML private TextField chatBox;

    @FXML private ImageView grid0;
    @FXML private ImageView grid1;
    @FXML private ImageView grid2;
    @FXML private ImageView grid3;
    @FXML private ImageView grid4;
    @FXML private ImageView grid5;
    @FXML private ImageView grid6;
    @FXML private ImageView grid7;
    @FXML private ImageView grid8;

    @FXML private ImageView grid00;
    @FXML private ImageView grid01;
    @FXML private ImageView grid02;
    @FXML private ImageView grid03;
    @FXML private ImageView grid04;
    @FXML private ImageView grid05;
    @FXML private ImageView grid06;
    @FXML private ImageView grid07;
    @FXML private ImageView grid08;
    @FXML private ImageView grid09;
    @FXML private ImageView grid10;
    @FXML private ImageView grid11;
    @FXML private ImageView grid12;
    @FXML private ImageView grid13;
    @FXML private ImageView grid14;
    @FXML private ImageView grid15;
    @FXML private ImageView grid16;
    @FXML private ImageView grid17;
    @FXML private ImageView grid18;
    @FXML private ImageView grid19;
    @FXML private ImageView grid20;
    @FXML private ImageView grid21;
    @FXML private ImageView grid22;
    @FXML private ImageView grid23;
    @FXML private ImageView grid24;
    @FXML private ImageView grid25;
    @FXML private ImageView grid26;
    @FXML private ImageView grid27;
    @FXML private ImageView grid28;
    @FXML private ImageView grid29;
    @FXML private ImageView grid30;
    @FXML private ImageView grid31;
    @FXML private ImageView grid32;
    @FXML private ImageView grid33;
    @FXML private ImageView grid34;
    @FXML private ImageView grid35;
    @FXML private ImageView grid36;
    @FXML private ImageView grid37;
    @FXML private ImageView grid38;
    @FXML private ImageView grid39;
    @FXML private ImageView grid40;
    @FXML private ImageView grid41;
    @FXML private ImageView grid42;
    @FXML private ImageView grid43;
    @FXML private ImageView grid44;
    @FXML private ImageView grid45;
    @FXML private ImageView grid46;
    @FXML private ImageView grid47;
    @FXML private ImageView grid48;
    @FXML private ImageView grid49;
    @FXML private ImageView grid50;
    @FXML private ImageView grid51;
    @FXML private ImageView grid52;
    @FXML private ImageView grid53;
    @FXML private ImageView grid54;
    @FXML private ImageView grid55;
    @FXML private ImageView grid56;
    @FXML private ImageView grid57;
    @FXML private ImageView grid58;
    @FXML private ImageView grid59;
    @FXML private ImageView grid60;
    @FXML private ImageView grid61;
    @FXML private ImageView grid62;
    @FXML private ImageView grid63;
    @FXML private ImageView grid64;
    @FXML private ImageView grid65;
    @FXML private ImageView grid66;
    @FXML private ImageView grid67;
    @FXML private ImageView grid68;
    @FXML private ImageView grid69;
    @FXML private ImageView grid70;
    @FXML private ImageView grid71;
    @FXML private ImageView grid72;
    @FXML private ImageView grid73;
    @FXML private ImageView grid74;
    @FXML private ImageView grid75;
    @FXML private ImageView grid76;
    @FXML private ImageView grid77;
    @FXML private ImageView grid78;
    @FXML private ImageView grid79;
    @FXML private ImageView grid80;

    @FXML private Media media;
    @FXML private MediaPlayer clickSfxPlayer;

    @FXML private Scene scene;
    @FXML private Parent root;
    @FXML private Stage stage;

    private ClientMain client;
    private int dimension;
    private String symbol;
    ArrayList<ImageView> grids;
    private Image symbolX;
    private Image symbolO;
    private int turn;

    @FXML
    public void initialize() {
        // Register this controller with ClientMain
        ClientMain.setGameController(this);
        initializeChat();
        // Get dimension from ClientMain (set during game creation)
        this.dimension = ClientMain.getCurrentGameDimension();
        
        if (dimension == 0) {
            System.out.println("Warning: Game dimension not set! Waiting for server information...");
            // Request dimension from server if not available
            if (ClientMain.getClient() != null && ClientMain.getCurrentGameId() != null) {
                ClientMain.getClient().sendMessage("get_game_info-" + ClientMain.getCurrentGameId());
            }
            return; // Don't initialize board yet
        }
        initializePlayerInfo();
        initializeGameBoard();
        
    }

    private void initializeChat() {
        if (chatArea != null) {
            chatArea.getItems().clear();
            // Add welcome message
            chatArea.getItems().add("=== Game Chat ===");
            chatArea.getItems().add("You can chat with other players here");
        }
        
        if (chatBox != null) {
            chatBox.setOnKeyPressed(this::handleChatKeyPressed);
            chatBox.setPromptText("Type your message...");
        }
        
        if (sendButton != null) {
            sendButton.setOnAction(event -> sendChatMessage());
            sendButton.setText("Send");
        }
    }

    private void initializePlayerInfo() {
        String player1 = ClientMain.getCurrentPlayer1();
        String player2 = ClientMain.getCurrentPlayer2();
        int player1Elo = ClientMain.getCurrentPlayer1Elo();
        int player2Elo = ClientMain.getCurrentPlayer2Elo();
        
        // Determine current player's symbol and info
        if (ClientMain.getClient() != null) {
            String currentUsername = ClientMain.getClient().getUsername();
            if (currentUsername.equals(player1)) {
                this.symbol = "X";
                System.out.println("You are Player 1 (X) - ELO: " + player1Elo);
                if (player2 != null) {
                    System.out.println("Opponent: " + player2 + " (O) - ELO: " + player2Elo);
                } else {
                    System.out.println("Waiting for opponent...");
                }
            } else if (currentUsername.equals(player2)) {
                this.symbol = "O";
                System.out.println("You are Player 2 (O) - ELO: " + player2Elo);
                System.out.println("Opponent: " + player1 + " (X) - ELO: " + player1Elo);
            }
        }
    }
        
    private void initializeGameBoard() {
        System.out.println("GameController initializing with dimension: " + dimension);
        
        // Initialize the game board based on dimension
        if (dimension == 3) {
            board3x3.setVisible(true);
            board9x9.setVisible(false);
            grids = new ArrayList<>(Arrays.asList(grid0, grid1, grid2, grid3, grid4, grid5, grid6, grid7, grid8));
        } else if (dimension == 9) {
            board3x3.setVisible(false);
            board9x9.setVisible(true);
            grids = new ArrayList<>(Arrays.asList(grid00, grid01, grid02, grid03, grid04, grid05, grid06, grid07,
                    grid08, grid09, grid10, grid11, grid12, grid13, grid14, grid15,
                    grid16, grid17, grid18, grid19, grid20, grid21, grid22, grid23,
                    grid24, grid25, grid26, grid27, grid28, grid29, grid30, grid31,
                    grid32, grid33, grid34, grid35, grid36, grid37, grid38, grid39,
                    grid40, grid41, grid42, grid43, grid44, grid45, grid46, grid47,
                    grid48, grid49, grid50, grid51, grid52, grid53, grid54, grid55,
                    grid56, grid57, grid58, grid59, grid60, grid61, grid62, grid63,
                    grid64, grid65, grid66, grid67, grid68, grid69, grid70, grid71,
                    grid72, grid73, grid74, grid75, grid76, grid77, grid78, grid79,
                    grid80));
        } else {
            System.out.println("Invalid dimension: " + dimension + ", defaulting to 3x3");
            dimension = 3;
            board3x3.setVisible(true);
            board9x9.setVisible(false);
            grids = new ArrayList<>(Arrays.asList(grid0, grid1, grid2, grid3, grid4, grid5, grid6, grid7, grid8));
        }

        // Initialize images and sound with proper error handling
        try {
            symbolX = new Image(getClass().getResourceAsStream("/com/bumba/tic_tac_toe/img/X.png"));
            symbolO = new Image(getClass().getResourceAsStream("/com/bumba/tic_tac_toe/img/O.png"));
            
            // Initialize sound with proper resource loading
            String clickSfx = getClass().getResource("/com/bumba/tic_tac_toe/sfx/click.mp3").toExternalForm();
            Media clickSound = new Media(clickSfx);
            clickSfxPlayer = new MediaPlayer(clickSound);
            
        } catch (Exception e) {
            System.err.println("Failed to load game resources: " + e.getMessage());
            // Continue without resources rather than failing
        }

        turn = 1;
        
        System.out.println("Game initialized successfully with " + dimension + "x" + dimension + " board");
    }

    // Method to update dimension when received from server
    public void setGameDimension(int dimension) {
        this.dimension = dimension;
        ClientMain.setCurrentGameInfo(ClientMain.getCurrentGameId(), dimension);
        Platform.runLater(() -> initializeGameBoard());
    }
    
    @FXML
    protected void onClick() {
        clickSfxPlayer.seek(clickSfxPlayer.getStartTime());
        clickSfxPlayer.play();
    }

    @FXML
    private void handleGameMove(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        onClick();

        int row = GridPane.getRowIndex(clickedButton) == null ? 0 : GridPane.getRowIndex(clickedButton);
        int col = GridPane.getColumnIndex(clickedButton) == null ? 0 : GridPane.getColumnIndex(clickedButton);

        int move = 0;
        if(dimension == 3) {
            move = row * 3 + col;
            board3x3.setDisable(true);
        } else if (dimension == 9) {
            move = row * 9 + col;
            board9x9.setDisable(true);
        }
        ClientMain.sendGameMove(String.valueOf(move));
        updateBoard(move);
    }

    @FXML
    private void sendChatMessage() {
        String message = chatBox.getText().trim();
        if (!message.isEmpty()) {
            ClientMain.sendChatMessage(message);
            chatBox.clear();
        }
    }

    @FXML
    private void handleChatKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendChatMessage();
        }
    }

    public void addChatMessage(String message) {
        Platform.runLater(() -> {
            if (chatArea != null) {
                chatArea.getItems().add(message);
                // Auto-scroll to bottom
                chatArea.scrollTo(chatArea.getItems().size() - 1);
            }
        });
    }

    private void moveFromServer(int move) {
        //missing processing of server response

        updateBoard(move);
        if(dimension == 3) {
            board3x3.setDisable(false);
        } else if (dimension == 9) {
            board9x9.setDisable(false);
        }
    }

    private void updateBoard(int move) {

        switch(symbol){
            case "X":
                if(turn%2==1){
                    grids.get(move).setImage(symbolX);
                }
                else grids.get(move).setImage(symbolO);
                break;
            case "O":
                if(turn%2==1){
                    grids.get(move).setImage(symbolO);
                }
                else grids.get(move).setImage(symbolX);
                break;
        }
        turn++;
    }

    // Method called when game ends (from server message)
    public void handleGameEnd(String gameEndInfo) {
        Platform.runLater(() -> {
            System.out.println("Game ended: " + gameEndInfo);
            
            // Show game end message for a few seconds, then return to lobby
            // You can add a dialog or label here to show the result
            
            // The game does not go back to lobby screen after ending, but instead have a return button for the player
            // placehilder for game end message
        });
    }


    @FXML
    private void backToLobbyScene() {
        try {
            root = FXMLLoader.load(getClass().getResource("/com/bumba/tic_tac_toe/lobbyScene.fxml"));
            scene = new Scene(root);
            stage = (Stage) ffButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
