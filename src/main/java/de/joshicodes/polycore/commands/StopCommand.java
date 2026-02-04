package de.joshicodes.polycore.commands;

import de.joshicodes.polycore.util.ChatColor;
import de.joshicodes.polycore.util.commands.Command;
import de.joshicodes.polycore.util.commands.CommandManager;
import de.joshicodes.polycore.util.commands.CommandSender;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop", "stops the server", true, "shutdown");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!sender.isAdmin()) return false;
        sender.sendMessage(ChatColor.RED + "Shutting down...");
        return true;
    }

    @Override
    public CommandManager.ExecutionResult successResult() {
        return CommandManager.ExecutionResult.STOP_SERVER;
    }

}
