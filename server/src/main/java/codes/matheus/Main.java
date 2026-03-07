package codes.matheus;

import codes.matheus.web.http.Server;
import codes.matheus.web.websocket.WebSocketServer;
import org.jetbrains.annotations.NotNull;

public class Main {
    public static void main(String[] args) {
        @NotNull Server server = new Server(8080);
        server.start();
        @NotNull WebSocketServer webSocketServer = new WebSocketServer(8081);
        webSocketServer.start();
    }
}