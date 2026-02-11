package de.joshicodes.polycore.commands;

import de.joshicodes.polycore.PolyCore;
import de.joshicodes.polycore.util.ChatColor;
import de.joshicodes.polycore.util.commands.Command;
import de.joshicodes.polycore.util.commands.CommandSender;

import java.util.List;

public class PluginsCommand extends Command {

    public PluginsCommand() {
        super("plugins", "View all plugins", "pl");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(!sender.isAdmin()) return false;
        final List<String> pluginNames = PolyCore.getInstance().getPluginManager().getPluginNames();
        sender.sendMessage(ChatColor.YELLOW + "Loaded plugins: (" + pluginNames.size() + ")");
        final StringBuilder builder = new StringBuilder();
        pluginNames.forEach(name -> builder.append(name).append(", "));
        builder.delete(builder.length() - 2, builder.length());
        sender.sendMessage("- " + ChatColor.GREEN + builder.toString());
        return true;
    }

}
