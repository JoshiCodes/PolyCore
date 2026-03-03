package de.joshicodes.polycore.plugins;

import de.joshicodes.polycore.PolyCore;
import de.joshicodes.polycore.util.ChatColor;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PluginManager {

    public static final File PLUGINS_FOLDER = new File("plugins");

    private final PolyCore core;
    private final ConcurrentHashMap<String, PolyPlugin> plugins;
    private final ConcurrentHashMap<String, URLClassLoader> loaders;

    public PluginManager(PolyCore core) {
        this.core = core;
        plugins = new ConcurrentHashMap<>();
        loaders = new ConcurrentHashMap<>();
        if(!PLUGINS_FOLDER.exists())
            PLUGINS_FOLDER.mkdirs();
    }

    public void loadPlugins() {
        File[] files = PLUGINS_FOLDER.listFiles((_dir, name) -> name.endsWith(".jar"));
        if(files == null) return;

        for(File file : files) {

            final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            URLClassLoader loader = null;
            try {
                loader = new PluginClassLoader(
                    new URL[]{file.toURI().toURL()},
                    this.getClass().getClassLoader()
                );

                try {

                    Thread.currentThread().setContextClassLoader(loader);

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
                            plugin.init(core, data, loader);

                            plugins.put(data.name(), plugin);
                            loaders.put(data.name(), loader);

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
                } finally {
                    Thread.currentThread().setContextClassLoader(originalClassLoader);
                }

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
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

    public void disablePlugins() {
        for(String name : plugins.keySet()) {
            final PolyPlugin plugin = plugins.get(name);
            core.getConsoleSender().sendMessage(ChatColor.GREEN + "Disabling Plugin: " + name);
            try {
                plugin.onDisable();
            }  catch(Exception e) {
                e.printStackTrace();
                core.getConsoleSender().sendMessage(ChatColor.RED + "Failed to disable plugin: " + name + " (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
            }
        }
        for(URLClassLoader classLoader : loaders.values()) {
            try {
                classLoader.close();
            } catch (IOException e) {
                e.printStackTrace();
                core.getConsoleSender().sendMessage(ChatColor.RED + "Failed to unload a classloader: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    public List<String> getPluginNames() {
        return new ArrayList<>(plugins.keySet());
    }

}
