package com.n19.ltmproject.server.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.command.CommandFactory;
import com.n19.ltmproject.server.manager.ClientManager;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;

/**
 * Handles communication with a single client.
 * This class is responsible for managing the connection, reading input from the client,
 * and sending output to the client. It also handles client disconnection and notifies
 * the ClientManager when a client disconnects.
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private final ClientManager clientManager;
    private final Gson gson = new Gson();
    @Setter
    @Getter
    private String username;

    @Setter
    @Getter
    private long playerId;

    private final String clientAddress;

    /**
     * Constructor to initialize the ClientHandler with a socket and a reference to the ClientManager.
     *
     * @param socket        the socket connected to the client
     * @param clientManager the manager that handles all connected clients
     */
    public ClientHandler(Socket socket, ClientManager clientManager) {
        this.socket = socket;
        this.clientManager = clientManager;
        this.clientAddress = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

        System.out.println("Client connected from: " + clientAddress);
    }

    /**
     * The main method of the thread that handles the client's communication.
     * It reads the client's credentials, processes them, and sends appropriate responses.
     */
    @Override
    public void run() {
        try {
            setupStreams();
            listenForRequests();
        } catch (IOException e) {
            System.out.println("[Error]: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void setupStreams() throws IOException {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * This method listens for requests from the client and processes them.
     * First type is raw string message, which is used for game invitation, and thread listening
     * Second is Request object, which is used for other requests, handle by CommandFactory.
     *
     */
    private synchronized void listenForRequests() throws IOException {
        String jsonRequest;
        while ((jsonRequest = input.readLine()) != null) {
            synchronized (this) {
                System.out.println("Received raw message from client: " + jsonRequest);

                if (jsonRequest.contains("Invite:")) {
                    handleGameInvitation(jsonRequest);
                } else if (jsonRequest.startsWith("STOP_LISTENING")) {
                    output.println(gson.toJson(null));
                } else if (isValidJson(jsonRequest)) {
                    processRequest(jsonRequest);
                } else {
                    handleInvalidJson();
                }
            }

        }
    }

    public void handleGameInvitation(String message) {
        if (message.contains("Invite:")) {
            String invitedPlayerName = message.split(":")[1];
            clientManager.invitePlayer(invitedPlayerName, message + " Đã gửi thành công");
        }
    }

    private boolean isValidJson(String json) {
        try {
            gson.fromJson(json, Object.class);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }

    private void processRequest(String jsonRequest) {
        Request request = gson.fromJson(jsonRequest, Request.class);
        logRequest(request);
        System.out.println(request.getAction());

        Command command = CommandFactory.getCommand(request.getAction(), this, clientManager);
        Response response = command.execute(request);
        output.println(gson.toJson(response));
    }

    private void logRequest(Request request) {
        System.out.println("---------------");
        System.out.println("Received request from client: " + clientAddress);
        if (!request.getAction().isEmpty() && !request.getParams().isEmpty()) {
            System.out.println("Action: " + request.getAction());
            System.out.println("Data: " + request.getParams());
        }
        System.out.println("---------------");
    }

    private void handleInvalidJson() {
        System.out.println("[Error]: Received invalid JSON format.");
        output.println(gson.toJson(Response.builder()
                .status("error")
                .message("Invalid JSON format.")
                .build()));
    }

    /**
     * Interrupts the thread and closes the connection.
     */
    @Override
    public void interrupt() {
        closeConnection();
        super.interrupt();
    }

    /**
     * Closes the connection with the client and notifies the ClientManager to remove this client.
     */
    private void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            System.out.println("Client disconnected.");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            clientManager.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }
}