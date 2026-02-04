package de.joshicodes.polycore.util.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class Packet {

    public String type;
    public JsonElement payload;

    public Packet(final String type, final JsonElement payload) {
        this.type = type;
        this.payload = payload;
    }

    public Packet(final String type, final String payload) {
        this.type = type;
        this.payload = new JsonPrimitive(payload);
    }

}
