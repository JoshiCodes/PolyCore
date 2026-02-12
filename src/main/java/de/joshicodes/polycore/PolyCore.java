package de.joshicodes.polycore;

import de.joshicodes.polycore.commands.PluginsCommand;
import de.joshicodes.polycore.commands.StartCommand;
import de.joshicodes.polycore.commands.StopCommand;
import de.joshicodes.polycore.game.GameEndpoint;
import de.joshicodes.polycore.game.GameManager;
import de.joshicodes.polycore.plugins.PluginManager;
import de.joshicodes.polycore.util.commands.CommandManager;
import de.joshicodes.polycore.commands.HelpCommand;
import de.joshicodes.polycore.util.commands.ConsoleSender;
import de.joshicodes.polycore.util.ChatColor;
import de.joshicodes.polycore.util.VersionUtil;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import jakarta.websocket.DeploymentException;
import org.glassfish.tyrus.server.Server;

import java.io.*;
import java.util.Objects;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class PolyCore {

    public static void main(String[] args) throws IOException {
        new PolyCore();
    }

    private static PolyCore instance;

    private final YamlDocument config;
    private final Server server;

    private final PluginManager pluginManager;
    private final Thread commandThread;
    private final CommandManager commandManager;

    private final ConsoleSender consoleSender = new ConsoleSender();

    PolyCore() throws IOException {
        instance = this;

        config = YamlDocument.create(
                new File("config.yml"),
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.yml")),
                UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build()
        );

        final String host = config.getString("application.host", "localhost");
        final int port = config.getInt("application.port", 3091);

        server = new Server(
                host,
                port,
                "/tetris",
                null,
                GameEndpoint.class
        );

        pluginManager = new PluginManager(this);

        commandManager = new CommandManager(this);

        try {
            consoleSender.sendMessage("");
            consoleSender.sendMessage(ChatColor.YELLOW + "Loading PolyCore...");

            consoleSender.sendMessage(ChatColor.YELLOW + "Starting websocket server...");
            server.start();
            new GameManager();
            consoleSender.sendMessage(ChatColor.YELLOW + "Done!");

            consoleSender.sendMessage(ChatColor.YELLOW + "Registering Commands...");
            commandManager.registerCommand(this, new HelpCommand());
            commandManager.registerCommand(this, new StopCommand());
            commandManager.registerCommand(this, new StartCommand());
            commandManager.registerCommand(this, new PluginsCommand());
            consoleSender.sendMessage(ChatColor.YELLOW + "Done!");

            consoleSender.sendMessage(ChatColor.YELLOW + "Loading Plugins...");
            pluginManager.loadPlugins();
            pluginManager.enablePlugins();
            consoleSender.sendMessage(ChatColor.YELLOW + "Done!");

            consoleSender.sendMessage("");
            consoleSender.sendMessage(ChatColor.GREEN + "==========================");
            consoleSender.sendMessage(ChatColor.GREEN + "  PolyCore Server v" + VersionUtil.VERSION);
            consoleSender.sendMessage(ChatColor.GREEN + "==========================");
            consoleSender.sendMessage(ChatColor.YELLOW + "Checking for updates...");
            VersionUtil.UpdateInfo updateInfo = VersionUtil.checkForUpdates();
            if(updateInfo != null) {
                consoleSender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "A new version of PolyCore is available! Download it here: " + updateInfo.downloadUrl());
            } else {
                consoleSender.sendMessage(ChatColor.GREEN + "You are running the latest version of PolyCore! Great!");
            }
            consoleSender.sendMessage("");
            consoleSender.sendMessage(ChatColor.GREEN + "Server started successfully!");
            consoleSender.sendMessage(ChatColor.GREEN + "Listening on " + host + ":" + port + ".");
            consoleSender.sendMessage(ChatColor.GREEN + "Type \"help\" for help!");


            commandThread = new Thread(() -> {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                while(true) {
                    System.out.print("> ");
                    try {
                        line = br.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    commandManager.tryExecuteConsoleCommand(this, consoleSender, line);
                }
            }, "Command-Thread");

            commandThread.start();

        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }

    }

    public void stop() {
        server.stop();
        consoleSender.sendMessage(ChatColor.RED + "Web-Server stopped.");
        commandThread.interrupt();
        consoleSender.sendMessage(ChatColor.RED + "Command-Thread stopped.");
        consoleSender.sendMessage(ChatColor.RED + "Shutting down PolyCore...");
        System.exit(0);
    }

    public ConsoleSender getConsoleSender() {
        return consoleSender;
    }

    public static PolyCore getInstance() {
        return instance;
    }

    public YamlDocument getConfig() {
        return config;
    }

    public Server getServer() {
        return server;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

}