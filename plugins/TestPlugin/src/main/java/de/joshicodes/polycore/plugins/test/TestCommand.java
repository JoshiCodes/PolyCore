package de.joshicodes.polycore.plugins.test;

import de.joshicodes.polycore.util.commands.Command;
import de.joshicodes.polycore.util.commands.CommandSender;

public class TestCommand extends Command {

    public TestCommand() {
        super("test", "This is a test");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("Test!");
        return true;
    }

}
