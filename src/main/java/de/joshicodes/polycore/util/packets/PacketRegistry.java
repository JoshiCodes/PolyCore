package de.joshicodes.polycore.util.packets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.joshicodes.polycore.game.GameManager;
import de.joshicodes.polycore.util.packet.Packet;
import jakarta.websocket.Session;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PacketRegistry {

    private final GameManager gameManager;
    private final Map<String, IncomingPacketHandler> handlers = new HashMap<>();

    public PacketRegistry(final GameManager gameManager, final String... packages) {
        this.gameManager = gameManager;
        for(String packageName : packages) {
            registerPackage(packageName);
        }
    }

    public void registerPackage(final String packageName) {
        System.out.println("[Registry] Scanning package: " + packageName + "...");
        Reflections reflections = new Reflections(packageName);

        // Finde alle Klassen, die das Interface implementieren
        Set<Class<? extends IncomingPacketHandler>> handlerClasses =
                reflections.getSubTypesOf(IncomingPacketHandler.class);

        for (Class<? extends IncomingPacketHandler> clazz : handlerClasses) {
            // Prüfe, ob die Klasse das @PacketType Etikett hat
            if (clazz.isAnnotationPresent(PacketType.class)) {
                try {
                    String type = clazz.getAnnotation(PacketType.class).value();
                    IncomingPacketHandler handler = clazz.getDeclaredConstructor().newInstance();
                    handlers.put(type, handler);
                    System.out.println("[Registry] Registered: " + type + " -> " + clazz.getSimpleName());
                } catch (Exception e) {
                    System.err.println("Failed to register handler: " + clazz.getName());
                }
            }
        }
    }

    public void handle(String type, Session session, JsonElement payload) {
        IncomingPacketHandler handler = handlers.get(type);
        if (handler != null) {
            Packet packet = handler.handle(session, payload);
            if(packet != null) {
                session.getAsyncRemote().sendText(new Gson().toJson(packet));
            }
        } else {
            // Fallback für High-Frequency Game-Befehle
            gameManager.handleInput(session, type);
        }
    }

}
