package de.joshicodes.polycore.util.commands;

import de.joshicodes.polycore.PolyCore;
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

    public List<Command> getCommands() {
        return commands;
    }

    public ExecutionResult tryExecuteConsoleCommand(PolyCore polyCore, ConsoleSender sender, String line) {
        if(line.startsWith("/"))
            line = line.substring(1);
        for(Command command : commands) {
            final String[] args = line.split(" ");
            final String label = args[0];
            if(command.getLabel().equalsIgnoreCase(label) || command.getAliases().stream().anyMatch(label::equalsIgnoreCase)) {
                boolean success = command.execute(sender, args);
                if(success) {
                    return command.successResult();
                }
                return ExecutionResult.FAILED;
            }
        }
        sender.sendMessage(ChatColor.RED + "Unknown command. Type \"/help\" for help.");
        return ExecutionResult.NO_COMMAND;
    }

    public enum ExecutionResult {
        /**
         * The command executed successfully.
         */
        SUCCESS,
        /**
         * The command failed to execute.
         */
        FAILED,
        /**
         * No command was found matching the input.
         */
        NO_COMMAND,
        /**
         * The Command is telling the server to stop.
         */
        STOP_SERVER
    }

}
