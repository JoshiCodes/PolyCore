package de.joshicodes.polycore.plugins.test;

import de.joshicodes.polycore.plugins.PluginData;
import de.joshicodes.polycore.plugins.PolyPlugin;

@PluginData(
        name = "TestPlugin",
        version = "1.0"
)
public class TestPlugin extends PolyPlugin {

    @Override
    public void onEnable() {
        System.out.println("TestPlugin enabled!");
        registerCommand(new TestCommand());
    }

    @Override
    public void onDisable() {

    }

}
