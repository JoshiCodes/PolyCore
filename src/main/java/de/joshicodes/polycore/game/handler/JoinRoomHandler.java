package de.joshicodes.polycore.game.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.polycore.game.GameManager;
import de.joshicodes.polycore.game.GameRoom;
import de.joshicodes.polycore.game.Player;
import de.joshicodes.polycore.util.packet.Packet;
import de.joshicodes.polycore.util.packet.incoming.IncomingPacketHandler;
import de.joshicodes.polycore.util.packet.incoming.PacketType;
import jakarta.websocket.Session;

@PacketType("JOIN_ROOM")
public class JoinRoomHandler implements IncomingPacketHandler {

    @Override
    public Packet handle(Session session, JsonElement payload) {
        if(!GameManager.getInstance().validateSession(session, true)) return null;
        if(!payload.isJsonObject()) {
            // Invalid payload
            return new Packet("CREATE_ERROR:INVALID", "Invalid payload.");
        }
        JsonObject data = payload.getAsJsonObject();
        if(!data.has("roomId")) {
            return new Packet("CREATE_ERROR:INVALID", "Invalid payload.");
        }
        final String roomId = data.get("roomId").getAsString();
        final Player player = GameManager.getInstance().getPlayer(session);
        if(player == null) return null;
        boolean success = GameManager.getInstance().joinRoom(player, roomId);
        if(!success) {
            return new Packet("JOIN_ERROR:FAILED", "Failed to join room.");
        }
        GameRoom room = GameManager.getInstance().getRoom(roomId);
        JsonObject obj = new JsonObject();
        obj.addProperty("roomId", roomId);
        obj.addProperty("currentPlayers", room.getPlayerCount());
        obj.addProperty("maxPlayers", room.getMaxPlayers());
        return new Packet("JOIN_SUCCESS", obj);
    }

}
