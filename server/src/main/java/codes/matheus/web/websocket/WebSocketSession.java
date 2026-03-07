package codes.matheus.web.websocket;

import codes.matheus.entity.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public final class WebSocketSession {
    private final @NotNull SocketChannel socket;
    private final @NotNull User user;

    public WebSocketSession(@NotNull SocketChannel socket, @NotNull User user) {
        this.socket = socket;
        this.user = user;
    }

    public @NotNull User getUser() {
        return user;
    }

    public void send(@NotNull String message) throws IOException {
        socket.write(WebSocketFrame.encode(message));
    }
}
