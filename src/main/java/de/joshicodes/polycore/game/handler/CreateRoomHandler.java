package de.joshicodes.polycore.game.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.polycore.game.GameManager;
import de.joshicodes.polycore.util.packet.Packet;
import de.joshicodes.polycore.util.packet.incoming.IncomingPacketHandler;
import de.joshicodes.polycore.util.packet.incoming.PacketType;
import jakarta.websocket.Session;

@PacketType("CREATE_ROOM")
public class CreateRoomHandler implements IncomingPacketHandler {
    @Override
    public Packet handle(Session session, JsonElement payload) {
        if(!GameManager.getInstance().validateSession(session, true)) return null;
        if(!payload.isJsonObject()) {
            // Invalid payload
            return new Packet("CREATE_ERROR:INVALID", "Invalid payload.");
        }
        JsonObject data = payload.getAsJsonObject();
        if(!data.has("room_name") || !data.has("max_players")) {
            // Missing parameters
            return new Packet("CREATE_ERROR:MISSING_PARAMS", "Missing room_name or max_players.");
        }
        String roomName = data.get("room_name").getAsString();
        int maxPlayers = data.get("max_players").getAsInt();
        String id = GameManager.getInstance().createRoom(session, roomName, maxPlayers);
        if(id == null) {
            // Creation failed
            return new Packet("CREATE_ERROR:FAILED", "Room creation failed.");
        }
        return new Packet("CREATE_SUCCESS", id);
    }
}
