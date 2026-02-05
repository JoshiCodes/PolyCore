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
        GameManager.getInstance().getPacketRegistry().handle(
                p.type,
                session,
                p.payload
        );
    }

    @OnClose
    public void onClose(Session session) {
        GameManager.getInstance().quit(session);
    }

}
