package de.joshicodes.polycore.util.packets;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PacketType {
    String value(); // Packet name, e.g. "CREATE_ROOM"
}
