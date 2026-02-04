package de.joshicodes.polycore.util.commands;

public interface CommandSender {

    void sendMessage(String message);
    String getName();
    boolean isPlayer();
    boolean isAdmin();

}
