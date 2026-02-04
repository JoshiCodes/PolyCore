package de.joshicodes.polycore.game;

import com.google.gson.Gson;
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
        Packet packet = new Packet("MESSAGE", message);
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
        return false;
    }

    public Session getSession() {
        return session;
    }

}
