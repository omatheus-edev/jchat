package codes.matheus;

import codes.matheus.server.Server;
import org.jetbrains.annotations.NotNull;

public class ServerMain {
    public static void main(String[] args) {
        @NotNull Server server = new Server();
        server.run();
    }
}