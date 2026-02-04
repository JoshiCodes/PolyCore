package de.joshicodes.polycore.util.commands;

import java.util.List;

public abstract class Command {

    private final String label;
    private final String description;
    private final List<String> aliases;

    private boolean isAdmin = false;

    public Command(final String label, final String description, final boolean isAdmin, final String... aliases) {
        this.label = label;
        this.description = description;
        this.isAdmin = isAdmin;
        this.aliases = List.of(aliases);
    }

    public Command(final String label, final String description, final String... aliases) {
        this(label, description, false, aliases);
    }

    public Command(final String label, final String description) {
        this(label, description, false);
    }

    public String getLabel() {
        return label;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public abstract boolean execute(CommandSender sender, String[] args); // args[0] is always the label

    public CommandManager.ExecutionResult successResult() {
        return CommandManager.ExecutionResult.SUCCESS;
    }

}
