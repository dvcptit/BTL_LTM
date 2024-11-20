package com.n19.ltmproject.client.service;

import com.google.gson.Gson;
import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.dto.Response;

import java.io.IOException;

@Deprecated
public class ReceiveInvitationService {
    private final ServerHandler serverHandler;
    private final Gson gson = new Gson();

    public ReceiveInvitationService(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    /**
     * Nhận lời mời từ server.
     *
     * @return Đối tượng Response nhận được từ server.
     * @throws IOException Nếu có lỗi trong quá trình nhận phản hồi.
     */
    public Response receiveInvitation() throws IOException {
        // Nhận phản hồi từ server
        String jsonResponse = serverHandler.receiveMessage();

        // Chuyển đổi JSON thành đối tượng Response
        return gson.fromJson(jsonResponse, Response.class);
    }
}
