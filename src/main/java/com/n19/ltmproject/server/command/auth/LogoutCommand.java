package com.n19.ltmproject.server.command.auth;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;


import com.n19.ltmproject.server.service.AuthService;

public class LogoutCommand implements Command {
    private final AuthService authService;

    public LogoutCommand() {
        this.authService = new AuthService();
    }

    @Override
    public Response execute(Request request) {

        System.out.println("LogoutCommand.execute() called");

        String username = (String) request.getParams().get("username");

        String message = authService.logoutPlayerService(username);

        if (message.contains("Logout successfully!")) {
            // Nếu đăng nhập thành công, trả về đối tượng Player trong response
            return Response.builder()
                    .status("OK")
                    .message("Logout successfully!")
                    .build();
        } else {
            // Nếu đăng nhập thất bại, trả về thông báo lỗi
            return Response.builder()
                    .status("FAILED")
                    .message(message)
                    .build();
        }

    }
}
