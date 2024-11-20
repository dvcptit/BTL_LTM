package com.n19.ltmproject.client.test_request.game;

import com.n19.ltmproject.client.handler.ServerHandler;
import com.n19.ltmproject.client.service.SendResultService;

import java.io.IOException;

public class SendResultTest {
    public static void main(String[] args) {
        // Khởi tạo ServerHandler
        ServerHandler serverHandler = new ServerHandler();
        serverHandler.connect("127.0.0.1", 1234);

        // Tạo SendResultService
        SendResultService sendResultService = new SendResultService(serverHandler);

        // Gửi kết quả trận đấu
        String matchId = "123";  // ID của trận đấu
        String result = "Team A won";  // Kết quả trận đấu
        try {
            // Gửi kết quả và nhận phản hồi
            String responseMessage = sendResultService.sendGameResult(matchId, result);
            // In ra phản hồi
            System.out.println("Server response: " + responseMessage);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
          //   Ngắt kết nối
            try {
                serverHandler.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
