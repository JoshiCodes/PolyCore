package de.joshicodes.polycore.util.packet.incoming;

import com.google.gson.JsonElement;
import de.joshicodes.polycore.util.packet.Packet;
import jakarta.websocket.Session;

public interface IncomingPacketHandler {
    Packet handle(Session session, JsonElement payload);
}
