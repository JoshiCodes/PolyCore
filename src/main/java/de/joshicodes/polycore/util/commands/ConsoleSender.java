package de.joshicodes.polycore.util.commands;

import de.joshicodes.polycore.util.ChatColor;

public class ConsoleSender implements CommandSender {

    @Override
    public void sendMessage(String message) {
        System.out.println(ChatColor.RESET + message + ChatColor.RESET);
    }

    @Override
    public String getName() {
        return "CONSOLE"; // Console has no name
    }

    @Override
    public boolean isPlayer() {
        return false; // Console is never a player
    }

    @Override
    public boolean isAdmin() {
        return true; // Console is always admin
    }

}
