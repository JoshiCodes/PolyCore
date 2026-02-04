package de.joshicodes.polycore.util.packet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameStatePayload {

    public Map<String, PlayerDTO> states = new ConcurrentHashMap<>();

}


