package de.joshicodes.polycore.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.joshicodes.polycore.util.ChatColor;
import de.joshicodes.polycore.util.commands.CommandSender;
import de.joshicodes.polycore.util.packet.Packet;
import jakarta.websocket.Session;

public class Player implements CommandSender {

    private final Session session;
    private final String name;

    Player(final Session session, final String name) {
        this.session = session;
        this.name = name;
    }

    public String getId() {
        return session.getId();
    }

    @Override
    public void sendMessage(String message) {
        String formatted = ChatColor.fromAnsi(message);
        // Then convert format codes to HTML
        String htmlMessage = ChatColor.toHtml(formatted);
        JsonObject payload = new JsonObject();
        payload.addProperty("text", htmlMessage);
        payload.addProperty("raw", ChatColor.strip(message)); // Plain text version
        Packet packet = new Packet("MESSAGE", payload);
        session.getAsyncRemote().sendText(new Gson().toJson(packet));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    public Session getSession() {
        return session;
    }

}
