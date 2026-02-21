package codes.matheus.message;

import codes.matheus.exception.ProtocolException;
import codes.matheus.user.Username;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public final class MessageChat implements Message {
    public static boolean validate(@NotNull String message) {
        if (message.isBlank() || !message.contains(":")) return false;
        @NotNull String[] parts = message.split(":", 2);
        if (parts.length < 2) {
            return false;
        } else return parts[0].equals("SERVER") || Username.validate(parts[0]);
    }

    private final @NotNull Type type;
    private final @NotNull Operation operation;
    private final @NotNull String sender;
    private final @NotNull String content;
    private final @NotNull Instant instant;

    MessageChat(@NotNull Operation operation, @NotNull String text, @NotNull Instant instant) {
        if (!validate(text)) {
            throw new ProtocolException("invalid message format, expected 'SENDER:CONTENT' but received: " + text);
        }
        this.type = Type.CHAT;
        this.operation = operation;
        @NotNull String[] parts = text.split(":", 2);
        this.sender = parts[0];
        this.content = parts[1];
        this.instant = instant;
    }

    @Override
    public @NotNull Type getType() {
        return type;
    }

    @Override
    public @NotNull Operation getOperation() {
        return operation;
    }

    public @NotNull String getSender() {
        return sender;
    }

    @Override
    public @NotNull String getContent() {
        return content;
    }

    @Override
    public @NotNull Instant getInstant() {
        return instant;
    }

    @Override
    public @NotNull JsonObject toJson() {
        @NotNull JsonObject json = new JsonObject();
        json.addProperty("sender", sender);
        json.addProperty("content", content);
        json.addProperty("instant", format());
        return json;
    }

    @Override
    public String toString() {
        return format() + " " + sender + ": " + content;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageChat that = (MessageChat) o;
        return Objects.equals(sender, that.sender) && Objects.equals(content, that.content) && Objects.equals(instant, that.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, content, instant);
    }
}
