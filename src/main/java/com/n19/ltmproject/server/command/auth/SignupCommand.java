package com.n19.ltmproject.server.command.auth;

import com.n19.ltmproject.server.command.Command;
import com.n19.ltmproject.server.model.dto.Request;
import com.n19.ltmproject.server.model.dto.Response;
import com.n19.ltmproject.server.service.AuthService;

public class SignupCommand implements Command {
    private final AuthService authService;

    public SignupCommand() {
        this.authService = new AuthService();
    }

    @Override
    public Response execute(Request request) {

        System.out.println("SignUpCommand.execute() called");

        String username = (String) request.getParams().get("username");
        String password = (String) request.getParams().get("password");
        String confirmPassword = (String) request.getParams().get("confirmPassword");

        String message = authService.signUpPlayerService(username, password, confirmPassword);

        if (message.contains("Đăng ký thành công!")) {
            // Nếu đăng nhập thành công, trả về đối tượng Player trong response
            return Response.builder()
                    .status("OK")
                    .message("Đăng ký thành công!")
                    .build();
        }

        return Response.builder()
                .status("FAILED")
                .message(message)
                .build();
    }
}
