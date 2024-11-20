package com.n19.ltmproject.client.service;

import com.google.gson.Gson;
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.dto.Request;
import com.n19.ltmproject.client.model.dto.Response;

import java.io.IOException;
import java.util.Map;

public class MessageService {

    private final ServerHandler serverHandler;
    private final Gson gson = new Gson();

    public MessageService(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public Response sendRequestAndReceiveResponse(String action, Map<String, Object> params) {
        Request request = new Request();
        request.setAction(action);
        request.setParams(params);

        String jsonRequest = gson.toJson(request);
        serverHandler.sendMessage(jsonRequest);

        try {
            String jsonResponse = serverHandler.receiveMessage();
            return gson.fromJson(jsonResponse, Response.class);
        } catch (IOException e) {
            System.err.println("Error sending request: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void sendRequestNoResponse(String action, Map<String, Object> params) {
        Request request = new Request();
        request.setAction(action);
        request.setParams(params);

        String jsonRequest = gson.toJson(request);
        serverHandler.sendMessage(jsonRequest);
    }
}
