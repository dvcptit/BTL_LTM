package com.n19.ltmproject.server.dao;

import com.n19.ltmproject.server.model.Player;
import com.n19.ltmproject.server.model.enums.PlayerStatus;
import com.n19.ltmproject.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class PlayerDao {

    private SessionFactory sessionFactory;

    public PlayerDao() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public void updatePlayerStatusById(long playerId, PlayerStatus playerStatus) {

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            transaction = session.beginTransaction();
            Player player = session.get(Player.class, playerId);
            if (player == null) {
                throw new IllegalArgumentException("Player with ID " + playerId + " does not exist.");
            }
            player.setStatus(playerStatus);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Player> getAllPlayers() {
        List<Player> players = null;

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = null;
            transaction = session.beginTransaction();
            players = session.createQuery("from Player", Player.class).list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return players;
    }
}
