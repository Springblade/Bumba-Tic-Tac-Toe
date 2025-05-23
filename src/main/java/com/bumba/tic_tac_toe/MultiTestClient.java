package com.bumba.tic_tac_toe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MultiTestClient {
    public static void main(String[] args) {
        String clientId = args.length > 0 ? args[0] : "client1";
        
        try {
            Socket socket = new Socket("localhost", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            System.out.println(clientId + " connected!");
            
            // Reader thread
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(clientId + " received: " + line);
                    }
                } catch (IOException e) {
                    System.out.println(clientId + " disconnected");
                }
            }).start();
            
            // Test sequence
            Thread.sleep(1000);
            out.println("login-" + clientId + "-password123");
            
            Thread.sleep(2000);
            out.println("chat-" + clientId + ":Hello from " + clientId);
            
            Thread.sleep(2000);
            out.println("move-" + clientId + "-" + (int)(Math.random() * 9));
            
            Thread.sleep(5000); // Keep alive to see other clients' messages
            socket.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}