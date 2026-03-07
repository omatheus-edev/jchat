package codes.matheus.chat;

import codes.matheus.exception.BroadcastException;
import codes.matheus.message.Message;
import codes.matheus.web.websocket.WebSocketSession;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

public final class Broadcast {
    private final @NotNull Set<WebSocketSession> sessions;

    public Broadcast(@NotNull Set<WebSocketSession> sessions) {
        this.sessions = sessions;
    }

    public void toAll(@NotNull Message message) {
        sessions.forEach(session -> {
            try {
                session.send(message.serialize());
            } catch (IOException e) {
                throw new BroadcastException("broadcast failed: " + e.getMessage());
            }
        });
    }

    public void toOthers(@NotNull WebSocketSession sender, @NotNull Message message) {
        sessions.stream().filter(session -> !sender.equals(session)).forEach(session -> {
            try {
                session.send(message.serialize());
            } catch (IOException e) {
                throw new BroadcastException("broadcast failed: " + e.getMessage());
            }
        });
    }

    public void toUser(@NotNull WebSocketSession receiver, @NotNull Message message) {
        sessions.stream().filter(session -> session.equals(receiver))
                .findFirst()
                .ifPresent(session -> {
                    try {
                        session.send(message.serialize());
                    } catch (IOException e) {
                        throw new BroadcastException("broadcast failed: " + e.getMessage());
                    }
        });
    }
}
