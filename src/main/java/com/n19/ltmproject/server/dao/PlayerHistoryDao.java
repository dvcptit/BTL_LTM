package com.n19.ltmproject.server.dao;

import com.n19.ltmproject.server.model.Player;
import com.n19.ltmproject.server.model.PlayerHistory;
import com.n19.ltmproject.server.model.dto.PlayerHistoryDto;
import com.n19.ltmproject.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerHistoryDao {
    private SessionFactory sessionFactory;

    public PlayerHistoryDao() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public List<PlayerHistoryDto> getAllPlayerHistory() {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<PlayerHistoryDto> playerHistoryDtos = new ArrayList<>();

        try {
            transaction = session.beginTransaction();

            // Create CriteriaBuilder
            CriteriaBuilder cb = session.getCriteriaBuilder();

            // Create CriteriaQuery for PlayerHistoryDto
            CriteriaQuery<PlayerHistoryDto> cq = cb.createQuery(PlayerHistoryDto.class);

            // Define root entities
            Root<PlayerHistory> playerHistoryRoot = cq.from(PlayerHistory.class);
            Root<Player> playerRoot = cq.from(Player.class);

            // Define join condition on playerId
            cq.select(cb.construct(
                            PlayerHistoryDto.class,
                            playerRoot.get("username"),
                            playerHistoryRoot.get("totalGames"),
                            playerHistoryRoot.get("wins"),
                            playerHistoryRoot.get("draws"),
                            playerHistoryRoot.get("losses"),
                            playerHistoryRoot.get("totalPoints")
                    )
            ).where(cb.equal(playerHistoryRoot.get("playerId"), playerRoot.get("id")));

            // Execute the query and get results
            playerHistoryDtos = session.createQuery(cq).getResultList();

            // Sort by total points in descending order
            playerHistoryDtos.sort((p1, p2) -> Integer.compare(p2.getTotalPoints(), p1.getTotalPoints()));

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return playerHistoryDtos;
    }
    public PlayerHistoryDto getPlayerHistoryById(long playerId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        PlayerHistoryDto playerHistoryDto = null;

        try {
            transaction = session.beginTransaction();

            // Create CriteriaBuilder
            CriteriaBuilder cb = session.getCriteriaBuilder();

            // Create CriteriaQuery for PlayerHistoryDto
            CriteriaQuery<PlayerHistoryDto> cq = cb.createQuery(PlayerHistoryDto.class);

            // Define root entities
            Root<Player> playerRoot = cq.from(Player.class);
            Root<PlayerHistory> playerHistoryRoot = cq.from(PlayerHistory.class);

            // Define join condition and select specific player's history based on playerId
            cq.select(cb.construct(
                            PlayerHistoryDto.class,
                            playerRoot.get("username"),
                            playerHistoryRoot.get("totalGames"),
                            playerHistoryRoot.get("wins"),
                            playerHistoryRoot.get("draws"),
                            playerHistoryRoot.get("losses"),
                            playerHistoryRoot.get("totalPoints")
                    ))
                    .where(cb.and(
                            cb.equal(playerRoot.get("id"), playerHistoryRoot.get("playerId")),
                            cb.equal(playerRoot.get("id"), playerId)
                    ));

            // Execute the query and get single result
            playerHistoryDto = session.createQuery(cq).uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return playerHistoryDto;
    }


    // Phương thức cập nhật lịch sử chơi game của người chơi
    public boolean updateGameHistory(long playerId, boolean isWin, boolean isDraw) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        boolean isUpdated = false;

        try {
            transaction = session.beginTransaction();

            // Tìm PlayerHistory theo playerId
            PlayerHistory playerHistory = session.createQuery("FROM PlayerHistory WHERE playerId = :playerId", PlayerHistory.class)
                    .setParameter("playerId", playerId)
                    .uniqueResult();

            if (playerHistory == null) {
                // Create a new PlayerHistory record if it does not exist
                playerHistory = new PlayerHistory();
                playerHistory.setPlayerId(playerId);
                playerHistory.setTotalGames(0);
                playerHistory.setTotalPoints(0);
                playerHistory.setWins(0);
                playerHistory.setLosses(0);
                playerHistory.setDraws(0);
            }

            // Cập nhật thông tin thắng, thua, hòa
            if (isWin) {
                playerHistory.setWins(playerHistory.getWins() + 1);
            } else if (isDraw) {
                playerHistory.setDraws(playerHistory.getDraws() + 1);
            } else {
                playerHistory.setLosses(playerHistory.getLosses() + 1);
            }

            // Cập nhật tổng số trận
            playerHistory.setTotalGames(playerHistory.getTotalGames() + 1);
            // Cập nhật tổng điểm (giả sử mỗi trận thắng được 3 điểm, hòa 1 điểm, thua 0 điểm)
            playerHistory.setTotalPoints(playerHistory.getTotalPoints() + (isWin ? 3 : (isDraw ? 1 : 0)));

            // Cập nhật vào cơ sở dữ liệu
            session.saveOrUpdate(playerHistory);
            transaction.commit();
            isUpdated = true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return isUpdated;
    }
}