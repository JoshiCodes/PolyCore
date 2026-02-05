package de.joshicodes.polycore.game.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.polycore.PolyCore;
import de.joshicodes.polycore.game.GameManager;
import de.joshicodes.polycore.game.Player;
import de.joshicodes.polycore.util.commands.CommandManager;
import de.joshicodes.polycore.util.packet.Packet;
import de.joshicodes.polycore.util.packets.IncomingPacketHandler;
import de.joshicodes.polycore.util.packets.PacketType;
import jakarta.websocket.Session;

@PacketType("CHAT")
public class ChatHandler implements IncomingPacketHandler {

    @Override
    public Packet handle(Session session, JsonElement payload) {
        if(!GameManager.getInstance().validateSession(session, true)) return null;
        if(!payload.isJsonObject()) {
            // Invalid payload
            return new Packet("CHAT_ERROR:INVALID", "Invalid payload.");
        }
        JsonObject data = payload.getAsJsonObject();
        if(!data.has("content") || data.get("content").getAsString().isEmpty()) {
            // Missing command
            return new Packet("CHAT_ERROR:MISSING", "Missing content.");
        }
        String command = data.get("content").getAsString();
        final Player player = GameManager.getInstance().getPlayer(session);
        if(player == null) {
            return null;
        }
        CommandManager.CommandResult result = PolyCore.getInstance().getCommandManager().tryExecutePlayerCommand(player, command);
        if(result != CommandManager.CommandResult.NOT_A_COMMAND) return null;
        // Chat Message
        GameManager.getInstance().broadcastChatMessage(player, command);
        return null; // No return packet, chat message packet is broadcast
    }

}
