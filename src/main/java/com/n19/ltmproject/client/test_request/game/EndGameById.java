package com.n19.ltmproject.client.test_request.game;

import com.google.gson.Gson;
import com.n19.ltmproject.client.model.dto.Request;
import com.n19.ltmproject.client.model.dto.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;


/**
 * This is an example how to test the server with a client.
 * Each request send to the server should have an "action", and a map of "params".
 * The "action" is a string that represents the action that the server should perform.
 * The "params" is a map of key-value pairs that contains the data needed for the action.
 * The class of test_request should match the server command
 * You can see the server commands in the class com.n19.ltmproject.server.ServerCommands
 *
 * For example:
 * A simple client that connects to the server and sends a test request.
 * The request contains the game ID and the scores of the two players.
 * The server will end the game with the given ID and update the scores of the players.
 */
public class EndGameById {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            // Create a test request
            Request request = new Request();
            request.setAction("endGameById");
            Map<String, Object> params = Map.of("gameId", 7L, "player1Score", 1L, "player2Score", 2L);
            request.setParams(params);

            String toJson = gson.toJson(request);

            // Send the request to the server
            output.println(toJson);

            // Read the response from the server
            String jsonResponse = input.readLine();
            Response response = gson.fromJson(jsonResponse, Response.class);

            // Print the response
            System.out.println("Response: " + response.getStatus() + " - " + response.getMessage());
            System.out.println(response.getData().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
