package codes.matheus.chat;

import codes.matheus.message.Message;
import codes.matheus.message.Type;
import codes.matheus.web.http.Server;
import codes.matheus.web.websocket.WebSocketSession;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class ChatRoom {
    private final @NotNull Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final @NotNull Broadcast broadcast;

    public ChatRoom() {
        this.broadcast = new Broadcast(sessions);
    }

    public void join(@NotNull WebSocketSession session) {
        sessions.add(session);
        Server.log.info(session.getUser().getUsername().getName() + " joined on chat");

        @NotNull String onlineList = sessions.stream()
                .map(s -> s.getUser().getUsername().getName())
                .collect(Collectors.joining(","));

        broadcast.toAll(new Message(Type.SERVER, "ONLINE_LIST:" + onlineList, "SERVER"));

        @NotNull Message joinMsg = new Message(Type.GERAL,
                session.getUser().getUsername().getName() + " joined the chat",
                session.getUser().getUsername().getName());
        broadcast.toAll(joinMsg);
    }

    public void leave(@NotNull WebSocketSession session) {
        sessions.remove(session);
        Server.log.info(session.getUser().getUsername().getName() + " left the chat");

        @NotNull String onlineList = sessions.stream()
                .map(s -> s.getUser().getUsername().getName())
                .collect(Collectors.joining(","));

        if (!onlineList.isEmpty()) {
            broadcast.toAll(new Message(Type.SERVER, "ONLINE_LIST:" + onlineList, "SERVER"));
        }

        @NotNull Message leaveMsg = new Message(Type.GERAL,
                session.getUser().getUsername().getName() + " left the chat",
                session.getUser().getUsername().getName());
        broadcast.toAll(leaveMsg);
    }

    public void broadcast(@NotNull WebSocketSession sender, @NotNull Message message) {
        broadcast.toOthers(sender, message);
    }

    public void sendTo(@NotNull String username, @NotNull Message message) {
        sessions.stream()
                .filter(s -> s.getUser().getUsername().getName().equals(username))
                .findFirst()
                .ifPresent(s -> broadcast.toUser(s, message));
    }
}
