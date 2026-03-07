package codes.matheus.chat;

import codes.matheus.message.Message;
import codes.matheus.web.websocket.WebSocketSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ChatHandler {
    private final @NotNull ChatRoom chatRoom;

    public ChatHandler(@NotNull ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void handle(@NotNull WebSocketSession session, @NotNull String frame) {
        @Nullable Message message = Message.deserialize(frame, session.getUser().getUsername().getName());
        if (message == null) return;

        switch (message.getType()) {
            case GERAL -> chatRoom.broadcast(session, message);
            case PRIVATE -> {
                @NotNull JsonObject obj = JsonParser.parseString(frame).getAsJsonObject();
                if (obj.has("target")) {
                    chatRoom.sendTo(obj.get("target").getAsString(), message);
                }
            }
        }
    }
}
