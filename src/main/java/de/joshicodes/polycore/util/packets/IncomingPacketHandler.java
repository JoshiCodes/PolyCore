package de.joshicodes.polycore.util.packets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.joshicodes.polycore.util.packet.Packet;
import jakarta.websocket.Session;

public interface IncomingPacketHandler {
    Packet handle(Session session, JsonElement payload);
}
