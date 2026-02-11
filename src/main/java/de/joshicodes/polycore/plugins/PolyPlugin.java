package de.joshicodes.polycore.plugins;

import de.joshicodes.polycore.PolyCore;
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
        this.core = core;
        this.name = data.name();
        this.version = data.version();
        this.folder = new File(PluginManager.PLUGINS_FOLDER, name);
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public void saveDefaults() throws IOException {
        final InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml");
        if(stream == null) return;
        config = YamlDocument.create(
                new File(folder, "config.yml"),
                stream
        );
    }

    public YamlDocument getConfig() {
        if(config == null) {
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
