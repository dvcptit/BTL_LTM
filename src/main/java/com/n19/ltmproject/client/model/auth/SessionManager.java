package com.n19.ltmproject.client.model.auth;

import com.n19.ltmproject.client.model.Player;
import lombok.Getter;
import lombok.Setter;

public class SessionManager {
    // Lấy thông tin người dùng hiện tại
    // Đặt thông tin người dùng hiện tại
    @Getter
    @Setter
    private static Player currentUser;

    // Xóa thông tin người dùng (đăng xuất)
    public static void clearSession() {
        currentUser = null;
    }
}
