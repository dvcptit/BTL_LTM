package com.n19.ltmproject.server.dao;

import com.n19.ltmproject.server.model.Player;
import com.n19.ltmproject.server.model.PlayerHistory;
import com.n19.ltmproject.server.model.enums.PlayerStatus;
import com.n19.ltmproject.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import org.mindrot.jbcrypt.BCrypt;

public class AuthDao {
    private SessionFactory sessionFactory;
    public AuthDao() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Player loginPlayerDao(String username, String password) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null; // Bắt đầu giao dịch

        try {
            // Bắt đầu giao dịch
            transaction = session.beginTransaction();

            // Sử dụng CriteriaBuilder để tạo truy vấn
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Player> query = builder.createQuery(Player.class);
            Root<Player> root = query.from(Player.class);

            // Thiết lập các điều kiện cho username và password
            Predicate usernamePredicate = builder.equal(root.get("username"), username);

            // Kết hợp cả hai điều kiện trong truy vấn
            query.select(root).where(builder.and(usernamePredicate));

            // Thực hiện truy vấn và lấy kết quả duy nhất (nếu có)
            Query q = session.createQuery(query);
            Player player;

            try {
                player = (Player) q.getSingleResult();
            } catch (NoResultException e) {
                // Người dùng không tồn tại
                return null;
            }

            // So sánh mật khẩu đã mã hóa
            if (player != null && BCrypt.checkpw(password, player.getPassword())) {
                player.setStatus(PlayerStatus.ONLINE);
                session.update(player);
            } else {
                return null; // Sai mật khẩu
            }

            transaction.commit();  // Hoàn tất giao dịch
            return player;  // Trả về đối tượng Player sau khi đã cập nhật trạng thái
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();  // Nếu có lỗi, rollback giao dịch
            }
            e.printStackTrace();
        } finally {
            session.close();  // Đóng session sau khi hoàn tất
        }
        return null;
    }

    public String signUpPlayerDao(String username, String password, String confirmPassword) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        // Kiểm tra password và confirmPassword có khớp nhau không
        if (!password.equals(confirmPassword)) {
            return "Password and confirmPassword do not match.";
        }

        try {
            // Bắt đầu giao dịch
            transaction = session.beginTransaction();

            // Sử dụng CriteriaBuilder để kiểm tra xem username đã tồn tại chưa
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Player> query = builder.createQuery(Player.class);
            Root<Player> root = query.from(Player.class);

            // Tìm người dùng với username đã tồn tại
            Predicate usernamePredicate = builder.equal(root.get("username"), username);
            query.select(root).where(usernamePredicate);

            // Thực hiện truy vấn
            Query q = session.createQuery(query);
            Player existingPlayer = null;

            try {
                existingPlayer = (Player) q.getSingleResult();
            } catch (NoResultException e) {
                // Nếu không tìm thấy người dùng nào, không có vấn đề gì
            }

            // Nếu người dùng đã tồn tại, trả về null hoặc thông báo lỗi
            if (existingPlayer != null) {
                return "Username already exists.";
            }

            // Mã hóa
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Nếu người dùng chưa tồn tại, tiến hành lưu người dùng mới
            Player newPlayer = new Player();
            newPlayer.setUsername(username);
            newPlayer.setPassword(hashedPassword);  // Lưu password, có thể mã hóa trước khi lưu nếu cần
            newPlayer.setStatus(PlayerStatus.OFFLINE);  // Set trạng thái ban đầu

            // Lưu người dùng mới vào cơ sở dữ liệu
            session.save(newPlayer);

            PlayerHistory playerHistory = PlayerHistory.builder()
                    .playerId(newPlayer.getId())
                    .totalPoints(0)
                    .totalGames(0)
                    .wins(0)
                    .losses(0)
                    .draws(0)
                    .build();

            // Lưu player history mới vào cơ sở dữ liệu
            session.save(playerHistory);

            transaction.commit();  // Hoàn tất giao dịch

            // Trả về đối tượng người dùng vừa được tạo
            return "Đăng ký thành công!";

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();  // Nếu có lỗi, rollback giao dịch
            }
            e.printStackTrace();
            return "Đăng ký thất bại";
        } finally {
            session.close();  // Đóng session sau khi hoàn tất
        }
    }

    public String logoutPlayerDao(String username) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            // Bắt đầu giao dịch
            transaction = session.beginTransaction();

            // Sử dụng CriteriaBuilder để tìm người dùng theo username
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Player> query = builder.createQuery(Player.class);
            Root<Player> root = query.from(Player.class);

            // Thiết lập điều kiện tìm kiếm
            Predicate usernamePredicate = builder.equal(root.get("username"), username);
            query.select(root).where(usernamePredicate);

            // Thực hiện truy vấn
            Query q = session.createQuery(query);
            Player player;

            try {
                player = (Player) q.getSingleResult();
            } catch (NoResultException e) {
                // Người dùng không tồn tại
                return null;
            }

            // Cập nhật trạng thái của người dùng thành OFFLINE
            if (player != null) {
                player.setStatus(PlayerStatus.OFFLINE);
                session.update(player);
            }

            transaction.commit();  // Hoàn tất giao dịch
            return "Logout successfully!";  // Đăng xuất thành công
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();  // Nếu có lỗi, rollback giao dịch
            }
            e.printStackTrace();
        } finally {
            session.close();  // Đóng session sau khi hoàn tất
        }
        return null;  // Đăng xuất thất bại
    }
}
