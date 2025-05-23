package com.bumba.tic_tac_toe.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class clientHandler implements Runnable {
    private Socket clientSocket;
    private List<clientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;

    public clientHandler(Socket socket, List<clientHandler> clients) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() {
        try {
            String inputLine;
//            if(flag){
//                send log in
//                username & password
//                encrypted?
//            }
            String[] input;
            while ((inputLine = in.readLine()) != null) {
                input = inputLine.split("-");
                if (input[0].equals("move")) {
                    //handle move
                } else if (input[0].equals("message")) {
                    //handle broadcast
                }
//                for (clientHandler aClient : clients) {
//                    aClient.out.println(inputLine);
//                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            synchronized (clients) {
                clients.remove(this);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
