package com.n19.ltmproject.client.handler;

import java.io.*;
import java.net.*;

public class ServerHandler {

    private static ServerHandler instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public static synchronized ServerHandler getInstance() {
        if (instance == null) {
            instance = new ServerHandler();
        }
        return instance;
    }

    public void init(String host, int port) {
        connect(host, port);
    }

    public void connect(String host, int port) {
        if (socket != null && socket.isConnected()) {
            // Socket is already connected, no need to reconnect
            return;
        }

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true); // Enable auto-flush
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Successfully connected to the server!"); // Successful connection
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
            System.out.println("Disconnected from the server.");
        } catch (IOException e) {
            System.out.println("Error while disconnecting: " + e.getMessage());
        }
    }


    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            System.out.println("Message sent: " + message);
        }
    }

    public String receiveMessage() throws IOException {
        if (in == null) {
            throw new IllegalStateException("Input stream is not initialized. Ensure connection is established.");
        }
        try {
            String message = in.readLine();
            if (message != null) {
                System.out.println("[ServerHandle] Received message: " + message);
            }
            return message;
        } catch (IOException e) {
            System.out.println("Error reading message from server: " + e.getMessage());
            return null;
        }
    }
}