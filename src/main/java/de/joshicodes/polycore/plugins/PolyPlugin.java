package de.joshicodes.polycore.plugins;

import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.File;

public abstract class PolyPlugin {

    public static final File PLUGINS_FOLDER = new File("plugins");

    private YamlDocument config;

    public abstract void onEnable();
    public abstract void onDisable();

}
