package com.n19.ltmproject.client.service;

import com.google.gson.Gson;
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.dto.Request;
import com.n19.ltmproject.client.model.dto.Response;

import java.io.IOException;
import java.util.Map;

@Deprecated
public class SendRequestMessage {

    private final ServerHandler serverHandler;
    private final Gson gson = new Gson();

    public SendRequestMessage(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public boolean sendRequest(String action, Map<String, Object> params) {
        Request request = new Request();
        request.setAction(action);
        request.setParams(params);

        String jsonRequest = gson.toJson(request);
        serverHandler.sendMessage(jsonRequest);

        try {
            String jsonResponse = serverHandler.receiveMessage();
            Response response = gson.fromJson(jsonResponse, Response.class);
            return response.getStatus().equalsIgnoreCase("OK");
        } catch (IOException e) {
            System.err.println("Error sending request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}