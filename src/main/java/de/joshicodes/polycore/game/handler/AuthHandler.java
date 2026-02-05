package de.joshicodes.polycore.game.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.polycore.game.GameManager;
import de.joshicodes.polycore.game.Player;
import de.joshicodes.polycore.util.packet.Packet;
import de.joshicodes.polycore.util.packets.IncomingPacketHandler;
import de.joshicodes.polycore.util.packets.PacketType;
import jakarta.websocket.Session;

@PacketType("AUTH")
public class AuthHandler implements IncomingPacketHandler {

    @Override
    public Packet handle(Session session, JsonElement payload) {
        if(GameManager.getInstance().playerExists(session)) {
            // Already authenticated
            return new Packet("AUTH_ERROR:ALREADY", "Already authenticated.");
        }
        if(!payload.isJsonObject()) {
            // Invalid payload
            return new Packet("AUTH_ERROR:INVALID", "Invalid payload.");
        }
        JsonObject data = payload.getAsJsonObject();
        if(data == null || !data.has("username")) {
            // Missing username
            return new Packet("AUTH_ERROR:MISSING_USERNAME", "Missing username.");
        }
        String username = data.get("username").getAsString();
        Player player = GameManager.getInstance().registerPlayer(session, username);
        if(player == null) {
            // Registration failed
            return new Packet("AUTH_ERROR:FAILED", "Registration failed. Maybe the Username is already in use.");
        }
        final JsonObject obj = new JsonObject();
        obj.addProperty("message", "Authenticated successfully.");
        obj.addProperty("username", username);
        return new Packet("AUTH_SUCCESS", obj);
    }

}
