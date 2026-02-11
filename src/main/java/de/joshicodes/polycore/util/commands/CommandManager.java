package de.joshicodes.polycore.util.commands;

import de.joshicodes.polycore.PolyCore;
import de.joshicodes.polycore.game.Player;
import de.joshicodes.polycore.plugins.PolyPlugin;
import de.joshicodes.polycore.util.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private final List<Command> commands;

    public CommandManager(PolyCore polyCore) {
        this.commands = new ArrayList<>();
    }

    public void registerCommand(PolyCore core, Command command) {
        core.getConsoleSender().sendMessage(ChatColor.YELLOW + "Registering command '" + command.getLabel() + "'...");
        commands.add(command);
    }

    public void registerCommand(PolyPlugin plugin, final Command command) {
        plugin.getCore().getConsoleSender().sendMessage(ChatColor.YELLOW + "Registering command '" + command.getLabel() + "'...");
        commands.add(command);
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void tryExecuteConsoleCommand(PolyCore polyCore, ConsoleSender sender, String line) {
        if(line.startsWith("/"))
            line = line.substring(1);
        for(Command command : commands) {
            final String[] args = line.split(" ");
            final String label = args[0];
            if(command.getLabel().equalsIgnoreCase(label) || command.getAliases().stream().anyMatch(label::equalsIgnoreCase)) {
                command.execute(sender, args);
                return;
            }
        }
        sender.sendMessage(ChatColor.RED + "Unknown command. Type \"/help\" for help.");
    }

    public CommandResult tryExecutePlayerCommand(final Player player, String line) {
        if(line.startsWith("/")) {
            line = line.substring(1);
            for(Command command : commands) {
                final String[] args = line.split(" ");
                final String label = args[0];
                if(command.getLabel().equalsIgnoreCase(label) || command.getAliases().stream().anyMatch(label::equalsIgnoreCase)) {
                    boolean success = command.execute(player, args);
                    if(success) {
                        return CommandResult.SUCCESS;
                    } else {
                        return CommandResult.FAILED;
                    }
                }
            }
        }
        return CommandResult.NOT_A_COMMAND;
    }

    public static enum CommandResult {
        SUCCESS,
        FAILED,
        NOT_FOUND,
        NOT_A_COMMAND
    }

}
