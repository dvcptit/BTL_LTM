package com.n19.ltmproject.client.test_request.player;

import com.google.gson.Gson;
import com.n19.ltmproject.client.model.dto.Request;
import com.n19.ltmproject.client.model.dto.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class Login {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            // Create a test request
            Request request = new Request();
            request.setAction("login");
            Map<String, Object> params = Map.of("username", "admin", "password", "123456");
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
