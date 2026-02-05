package de.joshicodes.polycore.game.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.polycore.game.GameManager;
import de.joshicodes.polycore.game.GameRoom;
import de.joshicodes.polycore.util.packet.Packet;
import de.joshicodes.polycore.util.packet.incoming.IncomingPacketHandler;
import de.joshicodes.polycore.util.packet.incoming.PacketType;
import jakarta.websocket.Session;

import java.util.concurrent.ConcurrentHashMap;

@PacketType("SHOW_ROOMS")
public class ShowRoomsHandler implements IncomingPacketHandler {

    @Override
    public Packet handle(Session session, JsonElement payload) {
        if(!GameManager.getInstance().validateSession(session, true)) return null;
        ConcurrentHashMap<String, GameRoom> rooms = GameManager.getInstance().getRooms();
        JsonObject obj = new JsonObject();
        JsonArray array = new JsonArray();
        for(String id : rooms.keySet()) {
            JsonObject roomObj = new JsonObject();
            roomObj.addProperty("id", id);
            final GameRoom room = rooms.get(id);
            roomObj.addProperty("max", room.getMaxPlayers());
            roomObj.addProperty("count", room.getPlayerCount());
            roomObj.addProperty("running", room.isRunning());
            array.add(roomObj);
        }
        obj.add("rooms", array);
        return new Packet("SHOW_ROOMS", obj);
    }

}
