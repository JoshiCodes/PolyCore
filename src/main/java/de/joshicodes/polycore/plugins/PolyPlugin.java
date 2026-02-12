package de.joshicodes.polycore.plugins;

import de.joshicodes.polycore.PolyCore;
import de.joshicodes.polycore.util.ChatColor;
import de.joshicodes.polycore.util.commands.Command;
import de.joshicodes.polycore.util.commands.CommandManager;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class PolyPlugin {

    private PolyCore core;
    private String name;
    private String version;
    private File folder;

    private YamlDocument config;

    void init(final PolyCore core, final PluginData data) {
        this.core = Objects.requireNonNull(core, "core must not be null");
        Objects.requireNonNull(data, "PluginData must not be null");
        String pluginName = Objects.requireNonNull(data.name(), "Plugin name must not be null").trim();
        String pluginVersion = Objects.requireNonNull(data.version(), "Plugin version must not be null").trim();
        if (pluginName.isEmpty()) {
            throw new IllegalArgumentException("Plugin name must not be empty");
        }
        if (!pluginName.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Plugin name '" + pluginName + "' contains invalid characters. Only letters, digits, '-' and '_' are allowed.");
        }
        if (pluginVersion.isEmpty()) {
            throw new IllegalArgumentException("Plugin version must not be empty");
        }
        this.name = pluginName;
        this.version = pluginVersion;
        this.folder = new File(PluginManager.PLUGINS_FOLDER, sanitizeName());
    }

    public abstract void onEnable();
    public abstract void onDisable();

    private String sanitizeName() {
        final String trimmed = Objects.requireNonNull(this.name, "Plugin name cannot be null!").trim();
        if(trimmed.isEmpty()) {
            throw new IllegalArgumentException("Plugin name cannot be empty!");
        }

        if(trimmed.contains("..")) {
            throw new IllegalArgumentException("Plugin name must not contain relative path segments!");
        }

        if (trimmed.contains("/") || trimmed.contains("\\") || trimmed.contains(File.separator)) {
            throw new IllegalArgumentException("Plugin name must not contain path separators");
        }
        return trimmed;

    }

    public void saveDefaults() throws IOException {
        try (final InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            if (stream == null) return;
            try {
                folder.mkdir();
            } catch (Exception e) {
                core.getConsoleSender().sendMessage(ChatColor.RED + "Failed to create plugin folder: " + e.getMessage());
                return;
            }
            config = YamlDocument.create(
                    new File(folder, "config.yml"),
                    stream
            );
        }
    }

    public YamlDocument getConfig() {
        if(config == null) {
            try {
                folder.mkdir();
            } catch (Exception e) {
                core.getConsoleSender().sendMessage(ChatColor.RED + "Failed to create plugin folder: " + e.getMessage());
                return null;
            }
            try {
                config = YamlDocument.create(new File(folder, "config.yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return config;
    }

    public void registerCommand(final Command command) {
        getCommandManager().registerCommand(this, command);
    }

    public CommandManager getCommandManager() {
        return core.getCommandManager();
    }

    public PolyCore getCore() {
        return core;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public File getFolder() {
        return folder;
    }

}
