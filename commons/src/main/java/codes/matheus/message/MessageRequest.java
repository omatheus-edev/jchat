package codes.matheus.message;

import codes.matheus.exception.ProtocolException;
import codes.matheus.user.Password;
import codes.matheus.user.Username;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public final class MessageRequest implements Message {
    public static boolean validate(@NotNull String message) {
        if (message.isBlank() || !message.contains(":")) return false;
        @NotNull String[] parts = message.split(":", 2);
        if (!Username.validate(parts[0])) {
            return false;
        } else return Password.validate(parts[1]);
    }

    private final @NotNull Type type;
    private final @NotNull Operation operation;
    private final @NotNull String content;
    private final @NotNull Instant instant;

    MessageRequest(@NotNull Operation operation, @NotNull String content, @NotNull Instant instant) {
        if (!validate(content)) {
            throw new ProtocolException("invalid request format, expected 'USERNAME:PASSWORD' but received: " + content);
        }
        this.type = Type.REQUEST;
        this.operation = operation;
        this.content = content;
        this.instant = instant;
    }

    @Override
    public @NotNull Type getType() {
        return type;
    }

    public @NotNull Operation getOperation() {
        return operation;
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
        json.addProperty("content", content);
        json.addProperty("instant", format());
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageRequest that = (MessageRequest) o;
        return Objects.equals(content, that.content) && Objects.equals(instant, that.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, instant);
    }
}
