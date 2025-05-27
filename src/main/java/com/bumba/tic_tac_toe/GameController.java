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
    
            dimension = 3;
            board3x3.setVisible(true);
        
            grids = new ArrayList<>(Arrays.asList(grid0, grid1, grid2, grid3, grid4, grid5, grid6, grid7, grid8));
    

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

    // @FXML
    // private void handleGameMove(ActionEvent event) {
    //     Button clickedButton = (Button) event.getSource();
    //     onClick();

    //     int row = GridPane.getRowIndex(clickedButton) == null ? 0 : GridPane.getRowIndex(clickedButton);
    //     int col = GridPane.getColumnIndex(clickedButton) == null ? 0 : GridPane.getColumnIndex(clickedButton);

    //     int move = row * 3 + col;

    //     // Immediately update the local GUI to show the player's move
    //     String currentUsername = ClientMain.getClient() != null ? ClientMain.getClient().getUsername() : "";
    //     String player1 = ClientMain.getCurrentPlayer1();
        
    //     // Determine which symbol to place for current player
    //     Image symbolToPlace;
    //     if (currentUsername.equals(player1)) {
    //         symbolToPlace = symbolX; // Player 1 uses X
    //     } else {
    //         symbolToPlace = symbolO; // Player 2 uses O
    //     }
        
    //     // Place the symbol immediately on the GUI
    //     if (move >= 0 && move < grids.size() && grids.get(move) != null) {
    //         grids.get(move).setImage(symbolToPlace);
    //         System.out.println("Placed " + (symbolToPlace == symbolX ? "X" : "O") + " at position " + move);
    //         return;
    //     }
         
        
    //     ClientMain.sendGameMove(String.valueOf(move));
    // }
    @FXML
    private void handleGameMove(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        onClick();

        int row = GridPane.getRowIndex(clickedButton) == null ? 0 : GridPane.getRowIndex(clickedButton);
        int col = GridPane.getColumnIndex(clickedButton) == null ? 0 : GridPane.getColumnIndex(clickedButton);

        int move = row * 3 + col;
        
        // Check if position is already occupied locally
        if (move >= 0 && move < grids.size() && grids.get(move) != null) {
            if (grids.get(move).getImage() != null) {
                System.out.println("Position " + move + " is already occupied!");
                return; // Don't send move to server
            }
        }
        
        // Check if it's our turn (client-side validation)
        String currentUsername = ClientMain.getClient() != null ? ClientMain.getClient().getUsername() : "";
        String player1 = ClientMain.getCurrentPlayer1();
        String player2 = ClientMain.getCurrentPlayer2();
        
        // Simple turn check - if board is disabled, it's not our turn
        if (board3x3.isDisabled()) {
            System.out.println("It's not your turn!");
            return;
        }

        // Disable board immediately when we make our move
        // board3x3.setDisable(true);
        System.out.println("Making move at position " + move + ", disabling board");
        
        // Send move to server - the server will validate and broadcast back
        ClientMain.sendGameMove(String.valueOf(move));
    }

    public void handleOpponentMove(int position, String playerWhoMoved) {
        System.out.println("Handling opponent move: " + playerWhoMoved + " at position " + position);
        
        // Get current user info
        String currentUsername = ClientMain.getClient() != null ? ClientMain.getClient().getUsername() : "";
        String player1 = ClientMain.getCurrentPlayer1();
        String player2 = ClientMain.getCurrentPlayer2();
        
        // Determine which symbol to place based on who moved
        Image symbolToPlace;
        if (playerWhoMoved.equals(player1)) {
            // Player 1 always uses X
            symbolToPlace = symbolX;
        } else {
            // Player 2 always uses O
            symbolToPlace = symbolO;
        }
        
        if (position >= 0 && position < grids.size() && grids.get(position) != null) {
            grids.get(position).setImage(symbolToPlace);
            System.out.println("Placed " + (symbolToPlace == symbolX ? "X" : "O") + " at position " + position);
            
        }

        // Re-enable board ONLY if it's now our turn (opponent just moved)
        if ((playerWhoMoved.equals(player1) && currentUsername.equals(player2)) ||
            (playerWhoMoved.equals(player2) && currentUsername.equals(player1))) {
            System.out.println("Re-enabling board - opponent moved, now our turn");
            board3x3.setDisable(false);
            System.out.println("Re-enabling successful");
        } else {
            board3x3.setDisable(true);
        }
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
        board3x3.setDisable(false);
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
