package de.joshicodes.polycore.game.handler;

import com.google.gson.JsonElement;
import de.joshicodes.polycore.util.packet.Packet;
import de.joshicodes.polycore.util.packet.incoming.IncomingPacketHandler;
import de.joshicodes.polycore.util.packet.incoming.PacketType;
import jakarta.websocket.Session;

@PacketType("PING")
public class PingHandler implements IncomingPacketHandler {

    @Override
    public Packet handle(Session session, JsonElement payload) {
        return new Packet("PONG", payload);
    }
}
