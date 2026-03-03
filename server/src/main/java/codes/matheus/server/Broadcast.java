package codes.matheus.server;

import codes.matheus.entity.Client;
import codes.matheus.exception.BroadcastException;
import codes.matheus.message.Message;
import codes.matheus.message.Protocol;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Set;

public final class Broadcast {
    private final @NotNull Protocol protocol = new Protocol();
    private final @NotNull Set<Client> clients;

    public Broadcast(@NotNull Set<Client> clients) {
        this.clients = clients;
    }

    public void toOthers(@NotNull Client client, @NotNull Message message) {
        @NotNull String encoded = protocol.encode(message);
        clients.forEach(c -> {
            if (c != client && c.getSocket().isOpen()) {
                try {
                    c.write(encoded);
                } catch (IOException e) {
                    throw new BroadcastException("broadcast failed: " + e.getMessage());
                }
            }
        });

    }

    public void toUser(@NotNull String username, @NotNull Message message) {
        @NotNull String encoded = protocol.encode(message);
        clients.stream().filter(c -> c.getAccount().getUser().getUsername().getName().equals(username))
                .findFirst()
                .ifPresent(c -> {
                    try {
                        c.write(encoded);
                    } catch (IOException e) {
                        throw new BroadcastException("broadcast failed: " + e.getMessage());
                    }
                });
    }

    public void toUser(@NotNull SocketChannel socket, @NotNull Message message, boolean isWebSocket) {
        try {
            @NotNull String encoded = protocol.encode(message);
            @NotNull ByteBuffer buffer = isWebSocket ? WebSocketFrame.encode(encoded) : ByteBuffer.wrap(encoded.getBytes());
            socket.write(buffer);
        } catch (IOException e) {
            throw new BroadcastException("broadcast failed: " + e.getMessage());
        }
    }
}
