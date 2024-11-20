package com.n19.ltmproject.server.dao;

import com.n19.ltmproject.server.model.Game;
import com.n19.ltmproject.server.model.Player;
import com.n19.ltmproject.server.model.dto.GameHistoryDto;
import com.n19.ltmproject.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameDao {

    private SessionFactory sessionFactory;

    public GameDao() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public List<GameHistoryDto> getAllGameData(String playerId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<GameHistoryDto> gameHistories = new ArrayList<>();

        try {
            transaction = session.beginTransaction();
            List<Player> players = session.createQuery("FROM Player", Player.class).list();
            if (playerId != null) {
                List<Game> games = session.createQuery("FROM Game", Game.class).list();
                for (Game game : games) {
                    String player1Name = "", player2Name = "";
                    long player1Id = 0, player2Id = 0;
                    for (Player player : players) {
                        if (game.getPlayer1Id() == player.getId()) {
                            player1Name = player.getUsername();
                            player1Id = player.getId();
                        }
                        else if (game.getPlayer2Id() == player.getId()) {
                            player2Name = player.getUsername();
                            player2Id = player.getId();
                        }
                    }
                    GameHistoryDto gameHistoryDto = new GameHistoryDto(game.getGameId(), player1Id, player1Name, game.getPlayer1Score(), player2Id, player2Name, game.getPlayer2Score(), game.getStartTime(), game.getEndTime());
                    gameHistories.add(gameHistoryDto);
                }
            }
            else {
                List<Game> games = session.createQuery("FROM Game g WHERE g.player1Id = :playerId OR g.player2Id = :playerId", Game.class)
                        .setParameter("playerId", playerId)
                        .list();
                for (Game game : games) {
                    String player1Name = "", player2Name = "";
                    long player1Id = 0, player2Id = 0;
                    for (Player player : players) {
                        if (game.getPlayer1Id() == player.getId()) {
                            player1Name = player.getUsername();
                            player1Id = player.getId();
                        }
                        else if (game.getPlayer2Id() == player.getId()) {
                            player2Name = player.getUsername();
                            player2Id = player.getId();
                        }
                    }
                    GameHistoryDto gameHistoryDto = new GameHistoryDto(game.getGameId(), player1Id, player1Name, game.getPlayer1Score(), player2Id, player2Name, game.getPlayer2Score(), game.getStartTime(), game.getEndTime());
                    gameHistories.add(gameHistoryDto);
                }
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        Collections.reverse(gameHistories);

        return gameHistories;
    }

    public Game createNewGame(long player1Id, long player2Id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        Game game = null;

        try {
            transaction = session.beginTransaction();
            game = new Game();
            game.setPlayer1Id(player1Id);
            game.setPlayer2Id(player2Id);
//            game.setStartTime(String.valueOf(new Date()));
            game.setStartTime(String.valueOf(new Timestamp(System.currentTimeMillis())));
            session.save(game);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return game;
    }

    public Game endGameById(long gameId, long player1Score, long player2Score) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        Game game = null;

        try {
            transaction = session.beginTransaction();
            game = session.get(Game.class, gameId);
            game.setPlayer1Score(player1Score);
            game.setPlayer2Score(player2Score);
            game.setEndTime(String.valueOf(new Timestamp(System.currentTimeMillis())));
            session.update(game);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        return game;
    }

    @Deprecated
    public String SendChatMessageDao(long currentPlayerId, long opponentPlayerId, String message) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null; // Bắt đầu giao dịch

        try {
            // Bắt đầu giao dịch
            transaction = session.beginTransaction();
            transaction.commit();  // Hoàn tất giao dịch
            return currentPlayerId + "-" + opponentPlayerId + "-" + message;
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
}
