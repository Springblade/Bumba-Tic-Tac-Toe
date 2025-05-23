package com.bumba.tic_tac_toe;

import com.bumba.tic_tac_toe.server.clientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {
    private static List<clientHandler> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);

        while (true) {
            Socket clientSocket = serverSocket.accept();

            clientHandler clientThread = new clientHandler(clientSocket, clients);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }
}
