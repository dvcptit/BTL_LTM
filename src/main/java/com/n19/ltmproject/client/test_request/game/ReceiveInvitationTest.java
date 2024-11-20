package com.n19.ltmproject.client.test_request.game;

import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.model.dto.Response;
import com.n19.ltmproject.client.service.ReceiveInvitationService;

import java.io.IOException;

public class ReceiveInvitationTest {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        // Khởi tạo ServerHandler
        ServerHandler serverHandler = new ServerHandler();
        serverHandler.connect(SERVER_ADDRESS, SERVER_PORT);

        // Tạo ReceiveInvitationService
        ReceiveInvitationService receiveInvitationService = new ReceiveInvitationService(serverHandler);

        try {
            // Nhận lời mời từ server
            Response response = receiveInvitationService.receiveInvitation();

            // In ra phản hồi
            System.out.println("Received Invitation Response: " + response.getStatus() + " - " + response.getMessage());
            System.out.println(response.getData().toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Ngắt kết nối
            try {
                serverHandler.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
