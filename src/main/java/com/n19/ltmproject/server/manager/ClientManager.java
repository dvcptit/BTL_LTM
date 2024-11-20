package com.n19.ltmproject.server.manager;

import com.n19.ltmproject.server.handler.ClientHandler;
import com.n19.ltmproject.server.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing the clients that are connected to the server.
 */
public class ClientManager {

    private final List<ClientHandler> clients = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();

    public ClientManager() {}

    public synchronized void addClient(ClientHandler clientHandler) {
        clients.add(clientHandler);
        System.out.println("There are " + clients.size() + " clients have connected");
    }

    public synchronized List<ClientHandler> getClients() {
        return new ArrayList<>(clients);
    }

    public synchronized ClientHandler getClientByPlayerIdAndUsername(long playerId, String username) {
        for (ClientHandler client : clients) {
            System.out.println("Checking client with ID: " + client.getPlayerId() + " and Username: " + client.getUsername());

            if (client.getPlayerId() == playerId) {
                if (username == null || client.getUsername() != null && client.getUsername().equals(username)) {
                    System.out.println("Match found for player ID " + playerId + (username != null ? " and username " + username : ""));
                    return client;
                }
            }
        }
        System.out.println("No match found for player ID " + playerId + (username != null ? " and username " + username : ""));
        return null;
    }

    // Mời người chơi được mời
    public void invitePlayer(String invitedPlayerName, String message) {
        System.out.println("Inviting player: " + invitedPlayerName);

        for (ClientHandler client : getClients()) {
            String clientUsername = client.getUsername().trim();

            if (clientUsername.equalsIgnoreCase(invitedPlayerName.trim())) {
                client.sendMessage(message + " INVITATION");
                System.out.println("Invite sent to " + invitedPlayerName);
                return;
            } else {
                System.out.println("No match for: " + clientUsername);
            }
        }
    }

    public void acceptInvitation(String inviter, String accepter) {
        for (ClientHandler client : getClients()) {
            String clientUsername = client.getUsername().trim();

            if (clientUsername.equalsIgnoreCase(inviter.trim())) {
                client.sendMessage(accepter);
                System.out.println(accepter + " ACCEPTED YOUR INVITATION");
                return;
            }
        }
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void addPlayer(Player player) {
        players.add(player);
        System.out.println("There are " + players.size() + " players have login");
        for(Player p : players) {
            System.out.println(p.getUsername());
        }
    }
}