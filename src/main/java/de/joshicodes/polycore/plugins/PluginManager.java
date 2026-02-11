package de.joshicodes.polycore.plugins;

import de.joshicodes.polycore.PolyCore;
import de.joshicodes.polycore.util.ChatColor;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class PluginManager {

    public static final File PLUGINS_FOLDER = new File("plugins");

    private final PolyCore core;
    private final Map<String, PolyPlugin> plugins;

    public PluginManager(PolyCore core) {
        this.core = core;
        plugins = new HashMap<>();
        if(!PLUGINS_FOLDER.exists())
            PLUGINS_FOLDER.mkdirs();
    }

    public void loadPlugins() {
        File[] files = PLUGINS_FOLDER.listFiles((dir, name) -> name.endsWith(".jar"));
        if(files == null) return;

        for(File file : files) {
            try {
                URLClassLoader loader = new URLClassLoader(
                        new URL[]{file.toURI().toURL()},
                        this.getClass().getClassLoader()
                );

                Reflections reflections = new Reflections(
                        new ConfigurationBuilder()
                                .addUrls(file.toURI().toURL())
                                .addClassLoaders(loader)
                );

                Set<Class<? extends PolyPlugin>> pluginClasses = reflections.getSubTypesOf(PolyPlugin.class);

                for(Class<? extends PolyPlugin> clazz : pluginClasses) {
                    if(clazz.isAnnotationPresent(PluginData.class)) {
                        final PluginData data = clazz.getAnnotation(PluginData.class);
                        final PolyPlugin plugin = clazz.getDeclaredConstructor().newInstance();
                        plugin.init(core, data);

                        plugins.put(data.name(), plugin);
                        core.getConsoleSender().sendMessage(ChatColor.GREEN + "Loaded Plugin: " + data.name());
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void enablePlugins() {
        for(String name : plugins.keySet()) {
            final PolyPlugin plugin = plugins.get(name);
            core.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabling Plugin: " + name);
            plugin.onEnable();
        }
    }

    public List<String> getPluginNames() {
        return new ArrayList<>(plugins.keySet());
    }

}
