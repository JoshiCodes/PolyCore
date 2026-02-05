package de.joshicodes.polycore.game;

import com.google.gson.Gson;
import de.joshicodes.polycore.util.packet.Packet;
import de.joshicodes.polycore.util.packet.incoming.PacketRegistry;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {

    private static GameManager INSTANCE;

    public static GameManager getInstance() {
        return INSTANCE;
    }

    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>(); // sessionId -> Player
    private final ConcurrentHashMap<String, String> loc = new ConcurrentHashMap<>(); // sessionId -> roomId
    private final ConcurrentHashMap<String, GameRoom> rooms = new ConcurrentHashMap<>(); // roomId -> GameRoom

    private final PacketRegistry packetRegistry;

    public GameManager() {
        INSTANCE = this;
        packetRegistry = new PacketRegistry(this, "de.joshicodes.polycore.game.handler");
    }

    public Player registerPlayer(final Session session, final String name) {
        if(players.containsKey(session.getId())) {
            return null; // Player already registered
        }
        if(players.values().stream().anyMatch(p -> p.getName().equalsIgnoreCase(name))) {
            return null; // Name already in use
        }
        Player player = new Player(session, name);
        players.put(session.getId(), player);
        return player;
    }

    public boolean joinRoom(final Player player, final String roomId) {
        if(!players.containsKey(player.getSession().getId())) {
            // New player - should not happen, player needs to authenticate first
            return false;
        }
        final GameRoom current = getRoomByPlayer(player);
        if(current != null) {
            // Player is already in a room, should not happen, but let's handle it gracefully
            current.removePlayerById(player.getSession().getId());
            loc.remove(player.getSession().getId());
        }
        GameRoom room = rooms.get(roomId);
        if(room == null) {
            return false;
        }
        if(room.playerJoin(player)) {
            loc.put(player.getSession().getId(), roomId);

            return true;
        } else {
            return false;
        }
    }

    public void handleInput(final Session session, final String cmd) {
        if(!validateSession(session, false)) return;
        final String roomId = loc.get(session.getId());
        if(roomId == null) return;
        final GameRoom room = rooms.get(roomId);
        if(room == null) return;
        room.handleInput(session, cmd);
    }

    /**
     * Validates the session, sending an AUTH_ERROR packet and optionally closing the session if invalid.
     * @param session the session to validate
     * @param endIfInvalid whether to close the session if invalid
     * @return true if the session is valid, false otherwise
     */
    public boolean validateSession(final Session session, boolean endIfInvalid) {
        if(playerExists(session)) { return true; }
        Packet packet = null;
        if(loc.containsKey(session.getId())) {
            packet = new Packet("AUTH_ERROR", "Invalid session. Please re-authenticate.");
        } else {
            packet = new Packet("AUTH_ERROR", "You must authenticate before sending other packets.");
        }
        session.getAsyncRemote().sendText(new Gson().toJson(packet));
        if(endIfInvalid) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Please authenticate before sending other packets."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public void leaveRoom(final String id) {
        final String roomId = loc.get(id);
        if(roomId == null) return;
        final GameRoom room = rooms.get(roomId);
        if(room == null) return;
        room.removePlayerById(id);
        loc.remove(id);
    }

    public void quit(Session session) {
        leaveRoom(session.getId());
        players.remove(session.getId());
    }

    public boolean playerExists(final Session session) {
        return players.containsKey(session.getId());
    }

    public String createRoom(Session session, String roomName, int maxPlayers) {
        if(!validateSession(session, false)) return null;
        String roomId = "r" + String.format("%04d", rooms.size() + 1);
        GameRoom room = new GameRoom(roomId, roomName, maxPlayers);
        rooms.put(roomId, room);
        Player player = players.get(session.getId());
        if(joinRoom(player, roomId)) return roomId;
        return null;
    }

    public Player getPlayer(Session session) {
        return players.get(session.getId());
    }

    /**
     * Broadcasts a chat message from a player to all players in the same room.
     * @param player the player sending the message
     * @param command the chat message
     */
    public void broadcastChatMessage(Player player, String command) {
        final String roomId = loc.get(player.getSession().getId());
        final GameRoom room = roomId != null ? rooms.get(roomId) : null;
        if(roomId == null || room == null) {
            broadcastChatToAll(player, command);
            return;
        }
        room.broadcastChatMessage(player, command);
    }

    private void broadcastChatToAll(Player player, String command) {
        for(Player all : players.values()) {
            if(loc.containsKey(all.getSession().getId())) continue; // In room
            all.sendMessage(player.getName() + ": " + command);
        }
    }

    public PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }

    public ConcurrentHashMap<String, GameRoom> getRooms() {
        return rooms;
    }

    public GameRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public GameRoom getRoomByPlayer(@NotNull final Player player) {
        final String roomId = loc.get(player.getSession().getId());
        return roomId != null ? rooms.get(roomId) : null;
    }

}
