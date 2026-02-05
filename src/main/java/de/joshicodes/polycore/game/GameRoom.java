package de.joshicodes.polycore.game;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.polycore.util.packet.GameStatePayload;
import de.joshicodes.polycore.util.packet.Packet;
import de.joshicodes.polycore.util.packet.PlayerDTO;
import de.joshicodes.polycore.util.packet.PlayerState;
import jakarta.websocket.Session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GameRoom {

    private final String id;
    private final String name;
    private final int maxPlayers;
    private final ConcurrentHashMap<String, PlayerState> players = new ConcurrentHashMap<>();
    private final ScheduledExecutorService loop = Executors.newSingleThreadScheduledExecutor();
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
        return true;
    }

    private void start() {
        running = true;
        loop.scheduleAtFixedRate(this::tick, 0, 500, java.util.concurrent.TimeUnit.MILLISECONDS); // TODO: Read from config or something
        broadcast("START", "Go!");
    }

    private void tick() {
        int alive = 0;
        PlayerState winner = null;
        for(PlayerState player : players.values()) {
            if(!player.isAlive) continue;
            alive++;
            winner = player;
            int lines = player.engine.tick();
            if(lines == -1) {
                player.isAlive = false;
                // TODO: Broadcast death
            } else if(lines > 1) {
                // TODO: Attack others
            }
        }
        if(players.size() > 1 && alive <= 1) {
            broadcast("WINNER", winner != null ? winner.name : "null");
            stop();
        }
        sendUpdate();
    }

    public void handleInput(final Session session, final String cmd) {
        final PlayerState player = players.get(session.getId());
        if(player == null || !player.isAlive) return;
        switch (cmd.toUpperCase()) {
            case "LEFT" -> player.engine.move(-1);
            case "RIGHT" -> player.engine.move(1);
            case "ROTATE" -> player.engine.rotate();
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
                            state.isAlive,
                            state.engine.getCurrentX(),
                            state.engine.getCurrentY(),
                            state.engine.getColorId()
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
        players.remove(session.getId());
    }

    public void removePlayerById(final String id) {
        players.remove(id);
    }

    public void broadcastChatMessage(final Player player, final String message) {
        JsonObject object = new JsonObject();
        object.addProperty("player", player.getName());
        object.addProperty("message", message);
        broadcast("CHAT", object);
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
