package com.n19.ltmproject.client.service;

import com.google.gson.Gson;
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.dto.Response;

import java.io.IOException;

@Deprecated
public class ReceiveResponseMessage {

    private final ServerHandler serverHandler;
    private final Gson gson = new Gson();

    public ReceiveResponseMessage(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public Response receiveResponse() throws IOException {
        // Nhận tin nhắn từ server
        String jsonResponse = serverHandler.receiveMessage();

        // Chuyển đổi JSON thành đối tượng Response
        if (jsonResponse != null) {
            return gson.fromJson(jsonResponse, Response.class);
        }
        return null;
    }
}
