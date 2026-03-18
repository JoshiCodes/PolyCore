package de.joshicodes.polycore.httpplugin;

import de.joshicodes.polycore.plugins.PluginData;
import de.joshicodes.polycore.plugins.PolyPlugin;
import de.joshicodes.polycore.util.ChatColor;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;

@PluginData(
        name = "HttpPlugin",
        version = "1.0.0"
)
public class HttpPlugin extends PolyPlugin {

    @Override
    public void onEnable() {

        try {
            saveDefaults();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final String host = getConfig().getString("host", "0.0.0.0");
        final int port = getConfig().getInt("port", 5678);

        System.out.println(1);

        final HttpServer server = HttpServer.createSimpleServer(null, host, port);
        server.getServerConfiguration().addHttpHandler(new HttpHandler() {
            @Override
            public void service(Request request, Response response) throws Exception {
                response.setContentType("text/plain");
                response.getWriter().write("Hello from HttpPlugin!");
            }
        });

        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        getCore().getConsoleSender().sendMessage(ChatColor.GREEN + "HttpPlugin started!");

    }

    @Override
    public void onDisable() {

    }

}
