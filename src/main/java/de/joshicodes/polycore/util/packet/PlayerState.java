package de.joshicodes.polycore.util.packet;

import de.joshicodes.polycore.game.GameEngine;
import de.joshicodes.polycore.util.commands.CommandSender;
import jakarta.websocket.Session;

public class PlayerState {

    public final Session session;
    public final String name;
    public final GameEngine engine;

    public PlayerState(final Session session, final String name) {
        this.session = session;
        this.name = name;
        this.engine = new GameEngine();
    }

    public void reset() {
        this.engine.reset();
    }

    public boolean isAlive() {
        return !engine.isGameOver();
    }

}
