package com.n19.ltmproject.server;

import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.manager.ClientManager;
import com.n19.ltmproject.server.util.HibernateUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final ClientManager clientManager = new ClientManager();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("[Server] Server is listening on port 1234");

            HibernateUtil.getSessionFactory();

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("[Server] New client connected");

                    ClientHandler clientHandler = new ClientHandler(socket, clientManager);
                    clientManager.addClient(clientHandler);
                    clientHandler.start();
                } catch (IOException e) {
                    System.out.println("[Server] Error accepting client connection: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("[Server] Server exception: " + ex.getMessage());
        } finally {
            HibernateUtil.shutdown();
        }
    }
}



