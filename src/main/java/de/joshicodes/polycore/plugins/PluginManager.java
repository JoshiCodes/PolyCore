package de.joshicodes.polycore.plugins;

import de.joshicodes.polycore.PolyCore;
import de.joshicodes.polycore.util.ChatColor;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PluginManager {

    public static final File PLUGINS_FOLDER = new File("plugins");

    private final PolyCore core;
    private final ConcurrentHashMap<String, PolyPlugin> plugins;

    public PluginManager(PolyCore core) {
        this.core = core;
        plugins = new ConcurrentHashMap<>();
        if(!PLUGINS_FOLDER.exists())
            PLUGINS_FOLDER.mkdirs();
    }

    public void loadPlugins() {
        File[] files = PLUGINS_FOLDER.listFiles((dir, name) -> name.endsWith(".jar"));
        if(files == null) return;

        for(File file : files) {
            try (URLClassLoader loader = new URLClassLoader(
                    new URL[]{file.toURI().toURL()},
                    this.getClass().getClassLoader()
            )) {

                Reflections reflections = new Reflections(
                        new ConfigurationBuilder()
                                .addUrls(file.toURI().toURL())
                                .addClassLoaders(loader)
                );

                Set<Class<? extends PolyPlugin>> pluginClasses = reflections.getSubTypesOf(PolyPlugin.class);

                for(Class<? extends PolyPlugin> clazz : pluginClasses) {
                    if(clazz.isAnnotationPresent(PluginData.class)) {
                        final PluginData data = clazz.getAnnotation(PluginData.class);
                        final String name = data.name();
                        if(plugins.containsKey(name)) {
                            core.getConsoleSender().sendMessage(ChatColor.RED + "Duplicate plugin '" + name + "' found. Will not load.");
                            continue;
                        }
                        final PolyPlugin plugin = clazz.getDeclaredConstructor().newInstance();
                        plugin.init(core, data);

                        plugins.put(data.name(), plugin);
                        core.getConsoleSender().sendMessage(ChatColor.GREEN + "Loaded Plugin: " + data.name());
                    }
                }

            } catch (SecurityException | LinkageError e) {
                core.getConsoleSender().sendMessage(
                        ChatColor.RED + "Critical error while loading plugin JAR '" + file.getName() + "': "
                                + e.getClass().getSimpleName() + " - " + e.getMessage()
                );
                throw e;
            } catch (Exception e) {
                core.getConsoleSender().sendMessage(
                        ChatColor.RED + "Failed to load plugin(s) from JAR '" + file.getName() + "': "
                                + e.getClass().getSimpleName() + " - " + e.getMessage()
                );
            }
        }
    }

    public void enablePlugins() {
        for(String name : plugins.keySet()) {
            final PolyPlugin plugin = plugins.get(name);
            core.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabling Plugin: " + name);
            try {
                plugin.onEnable();
            } catch(Exception e) {
                core.getConsoleSender().sendMessage(ChatColor.RED + "Failed to enable plugin: " + name + " (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
                plugin.onDisable();
                e.printStackTrace();
            }
        }
    }

    public List<String> getPluginNames() {
        return new ArrayList<>(plugins.keySet());
    }

}
