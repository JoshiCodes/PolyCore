package de.joshicodes.polycore.plugins.test;

import de.joshicodes.polycore.plugins.PluginData;
import de.joshicodes.polycore.plugins.PolyPlugin;

import java.io.IOException;

@PluginData(
        name = "TestPlugin",
        version = "1.0"
)
public class TestPlugin extends PolyPlugin {

    @Override
    public void onEnable() {
        System.out.println("TestPlugin enabled!");
        registerCommand(new TestCommand());

        try {
            saveDefaults();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        System.out.println("TestPlugin disabled!");
    }

}
