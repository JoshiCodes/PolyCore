package de.joshicodes.polycore.commands;

import de.joshicodes.polycore.game.GameManager;
import de.joshicodes.polycore.game.GameRoom;
import de.joshicodes.polycore.game.Player;
import de.joshicodes.polycore.util.ChatColor;
import de.joshicodes.polycore.util.commands.Command;
import de.joshicodes.polycore.util.commands.CommandSender;

public class StartCommand extends Command {

    public StartCommand() {
        super("start", "Starts the game");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) return false; // Only players can start the game
        final GameRoom room = GameManager.getInstance().getRoomByPlayer(player);
        if(room == null) {
            player.sendMessage(ChatColor.RED + "You are not in a game!");
            return true;
        }
        if(!room.isRunning()) {
            room.start();
            player.sendMessage(ChatColor.GREEN + "Game started!");
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "The game is already running!");
            return true;
        }
    }

}
