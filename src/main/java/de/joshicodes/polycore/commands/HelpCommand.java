package de.joshicodes.polycore.commands;

import de.joshicodes.polycore.PolyCore;
import de.joshicodes.polycore.util.commands.Command;
import de.joshicodes.polycore.util.commands.CommandSender;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "shows you a list of all commands");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("Available commands:");
        for(Command cmd : PolyCore.getInstance().getCommandManager().getCommands()) {
            if(cmd.isAdmin() && !sender.isAdmin()) continue;
            sender.sendMessage(cmd.getLabel() + " - " + cmd.getDescription());
        }
        return true;
    }

}
