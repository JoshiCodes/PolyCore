package de.joshicodes.polycore.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.joshicodes.polycore.PolyCore;
import de.joshicodes.polycore.util.commands.CommandManager;
import de.joshicodes.polycore.util.packet.Packet;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint("/game")
public class GameEndpoint {

    private final Gson gson = new Gson();

    @OnMessage
    public void onMessage(final String message, final Session session) {
        Packet p;
        try {
            p = gson.fromJson(message, Packet.class);
        } catch (JsonSyntaxException e) {
            // Invalid JSON received
            return;
        }
        switch (p.type) {
            case "PING" -> {
                Packet pong = new Packet("PONG", p.payload);
                session.getAsyncRemote().sendText(gson.toJson(pong));
                return;
            }
            case "AUTH" -> {
                if(GameManager.getInstance().playerExists(session)) {
                    // Already authenticated
                    Packet pong = new Packet("AUTH_ERROR:ALREADY", "Already authenticated.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    return;
                }
                if(!p.payload.isJsonObject()) {
                    // Invalid payload
                    Packet pong = new Packet("AUTH_ERROR:INVALID", "Invalid payload.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    return;
                }
                JsonObject data = p.payload.getAsJsonObject();
                if(data == null || !data.has("username")) {
                    // Missing username
                    Packet pong = new Packet("AUTH_ERROR:MISSING_USERNAME", "Missing username.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    return;
                }
                String username = data.get("username").getAsString();
                Player player = GameManager.getInstance().registerPlayer(session, username);
                if(player == null) {
                    // Registration failed
                    Packet pong = new Packet("AUTH_ERROR:FAILED", "Registration failed. Maybe the Username is already in use.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    return;
                }
                final JsonObject obj = new JsonObject();
                obj.addProperty("message", "Authenticated successfully.");
                obj.addProperty("username", username);
                Packet pong = new Packet("AUTH_SUCCESS", obj);
                session.getAsyncRemote().sendText(gson.toJson(pong));
                return;
            }
            case "CHAT" -> {
                if(!GameManager.getInstance().validateSession(session, true)) return;
                if(!p.payload.isJsonObject()) {
                    // Invalid payload
                    Packet pong = new Packet("CHAT_ERROR:INVALID", "Invalid payload.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    return;
                }
                JsonObject data = p.payload.getAsJsonObject();
                if(!data.has("content") || data.get("content").getAsString().isEmpty()) {
                    // Missing command
                    Packet pong = new Packet("CHAT_ERROR:MISSING", "Missing content.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    return;
                }
                String command = data.get("content").getAsString();
                final Player player = GameManager.getInstance().getPlayer(session);
                if(player == null) {
                    return;
                }
                CommandManager.CommandResult result = PolyCore.getInstance().getCommandManager().tryExecutePlayerCommand(player, command);
                if(result != CommandManager.CommandResult.NOT_A_COMMAND) return;
                // Chat Message
                GameManager.getInstance().broadcastChatMessage(player, command);
            }
            case "CREATE_ROOM" -> {
                if(!GameManager.getInstance().validateSession(session, true)) return;
                if(!p.payload.isJsonObject()) {
                    // Invalid payload
                    Packet pong = new Packet("CREATE_ERROR:INVALID", "Invalid payload.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    return;
                }
                JsonObject data = p.payload.getAsJsonObject();
                if(!data.has("room_name") || !data.has("max_players")) {
                    // Missing parameters
                    Packet pong = new Packet("CREATE_ERROR:MISSING_PARAMS", "Missing room_name or max_players.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    return;
                }
                String roomName = data.get("room_name").getAsString();
                int maxPlayers = data.get("max_players").getAsInt();
                String id = GameManager.getInstance().createRoom(session, roomName, maxPlayers);
                if(id == null) {
                    // Creation failed
                    Packet pong = new Packet("CREATE_ERROR:FAILED", "Room creation failed.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    return;
                }
                Packet pong = new Packet("CREATE_SUCCESS", id);
                session.getAsyncRemote().sendText(gson.toJson(pong));
            }
            case "MOVE_LEFT" -> GameManager.getInstance().handleInput(session, "LEFT");
            case "MOVE_RIGHT" -> GameManager.getInstance().handleInput(session, "RIGHT");
            case "ROTATE" -> GameManager.getInstance().handleInput(session, "ROTATE");
            default -> {
                // Unknown packet type
                if(!GameManager.getInstance().playerExists(session)) {
                    Packet pong = new Packet("AUTH_ERROR", "Please authenticate before sending other packets.");
                    session.getAsyncRemote().sendText(gson.toJson(pong));
                    try {
                        session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Please authenticate before sending other packets."));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                // Handle other game-related packets here
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        GameManager.getInstance().quit(session);
    }

}
