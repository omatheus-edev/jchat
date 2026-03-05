package codes.matheus.http;

import codes.matheus.http.routes.ChatRoute;
import com.jlogm.Logger;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class Server {
    public static final @NotNull Logger log = Logger.create(Server.class);
    private final int port;
    private final @NotNull ChatRoute chat;

    public Server(int port) {
        this.port = port;
        this.chat = new ChatRoute();
    }

    public void start() {
        try {
            @NotNull HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", chat);
            log.info("Registered endpoint " + "\"/\" " + "(GET)");

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            log.info("HTTP server running on http://localhost:" + port + "/");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
