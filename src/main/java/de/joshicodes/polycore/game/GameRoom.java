package de.joshicodes.polycore.game;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.polycore.util.packet.*;
import jakarta.websocket.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GameRoom {

    private final String id;
    private final String name;
    private final int maxPlayers;
    private final ConcurrentHashMap<String, PlayerState> players = new ConcurrentHashMap<>();
    private ScheduledExecutorService loop;
    private final Gson gson = new Gson();
    private boolean running = false;

    public GameRoom(final String id, final String name, final int maxPlayers) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
    }

    public synchronized boolean playerJoin(final Player player) {
        if(players.size() >= maxPlayers) return false;
        if(players.containsKey(player.getSession().getId())) return false;
        players.put(player.getSession().getId(), new PlayerState(player.getSession(), player.getName()));
        broadcast("JOIN", player.getName());
        return true;
    }

    public void start() {
        if(isRunning()) return;
        players.values().forEach(PlayerState::reset);
        running = true;
        loop = Executors.newSingleThreadScheduledExecutor();
        loop.scheduleAtFixedRate(this::tick, 0, 500, java.util.concurrent.TimeUnit.MILLISECONDS); // TODO: Read from config or something
        broadcast("START", "Go!");
    }

    private void tick() {
        int alive = 0;
        PlayerState winner = null;
        Map<String, Integer> pendingAttacks = new HashMap<>();
        for(PlayerState player : players.values()) {
            if(!player.isAlive()) continue;
            alive++;
            winner = player;
            int lines = player.engine.tick();
            if(lines == -1 || !player.isAlive()) {
                broadcast("DEATH", player.name);
            } else if(lines > 1) {
                // Calculate garbage to send (lines - 1 is common, or use a table)
                int garbageToSend = calculateGarbage(lines);

                // Queue attacks for other players
                for(PlayerState other : players.values()) {
                    if(other != player && other.isAlive()) {
                        pendingAttacks.merge(other.session.getId(), garbageToSend, Integer::sum);
                    }
                }

                // Notify about the attack
                broadcast("ATTACK", gson.toJsonTree(new AttackPayload(player.name, garbageToSend)));
            }
        }

        // Apply pending garbage attacks
        for(Map.Entry<String, Integer> entry : pendingAttacks.entrySet()) {
            PlayerState target = players.get(entry.getKey());
            if(target != null && target.isAlive()) {
                target.engine.queueGarbage(entry.getValue());
            }
        }

        if(alive < 1 || (players.size() > 1 && alive == 1)) {
            broadcast("WINNER", winner != null ? winner.name : "null");
            stop();
        }
        sendUpdate();
    }

    private int calculateGarbage(int linesCleared) {
        // Common Tetris attack table
        return switch(linesCleared) {
            case 2 -> 1;  // Double sends 1 garbage
            case 3 -> 2;  // Triple sends 2 garbage
            case 4 -> 4;  // Tetris sends 4 garbage
            default -> 0;
        };
    }

    public void handleInput(final Session session, final String cmd) {
        final PlayerState player = players.get(session.getId());
        if(player == null || !player.isAlive()) return;
        switch (cmd.toUpperCase()) {
            case "LEFT" -> player.engine.move(-1);
            case "RIGHT" -> player.engine.move(1);
            case "DOWN" -> player.engine.down();
            case "DROP" -> player.engine.drop();
            case "ROTATE" -> player.engine.rotate();
            case "HOLD" -> player.engine.holdPiece();
            default -> {
                return;
            }
        }
        sendUpdate();
    }

    private void sendUpdate() {
        GameStatePayload payload = new GameStatePayload();
        players.forEach((id, state) -> {
            payload.states.put(
                    state.name,
                    new PlayerDTO(
                            state.engine.getBoard(),
                            state.isAlive(),
                            state.engine.getCurrentX(),
                            state.engine.getCurrentY(),
                            state.engine.getColorId(),
                            state.engine.getCurrentShape(),
                            state.engine.getPoints(),
                            state.engine.getNextPiece(),
                            state.engine.getHoldPiece()
                    )
            );
        });
        broadcast("UPDATE", gson.toJsonTree(payload));
    }

    private void broadcast(final String type, final String payload) {
        broadcast(new Packet(type, payload));
    }

    private void broadcast(final String type, final JsonElement payload) {
        broadcast(new Packet(type, payload));
    }

    private void broadcast(final Packet packet) {
        players.values().forEach(player -> {
            if(player.session.isOpen()) {
                player.session.getAsyncRemote().sendText(gson.toJson(packet));
            }
        });
    }

    public void stop() {
        running = false;
        loop.shutdown();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public void removePlayer(final Session session) {
        removePlayerById(session.getId());
    }

    public void removePlayerById(final String id) {
        players.remove(id);

        if(isEmpty()) {
            stop();
        } else {
            broadcast("LEAVE", id);
        }
    }

    public void broadcastChatMessage(final Player player, final String message) {
        for(PlayerState p : players.values()) {
            final Player all = GameManager.getInstance().getPlayer(p.session);
            all.sendMessage(player.getName() + ": " + message);
        }
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean isRunning() {
        return running;
    }

}
