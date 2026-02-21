package codes.matheus.message;

import codes.matheus.exception.ProtocolException;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public final class MessageResponse implements Message {
    public static boolean validate(@NotNull String message) {
        if (message.isBlank() ||!message.contains("|")) return false;
        @NotNull String[] parts = message.split("\\|", 2);
        return parts.length == 2 || !parts[0].isBlank() || !parts[1].isBlank();
    }

    private final @NotNull Type type;
    private final @NotNull Operation operation;
    private final @NotNull String content;
    private final @NotNull MessageStatus status;
    private final @NotNull Instant instant;

    MessageResponse(@NotNull Operation operation, @NotNull String text, @NotNull Instant instant) {
        if (!validate(text)) {
            throw new ProtocolException("invalid response format, expected 'STATUS|CONTENT' but received: " + text);
        }
        this.type = Type.RESPONSE;
        this.operation = operation;
        this.instant = instant;
        @NotNull String[] parts = text.split("\\|", 2);
        this.status = parse(parts[0]);
        this.content = parts[1];
    }

    private @NotNull MessageStatus parse(@NotNull String input) {
        if (input.matches("\\d+")) {
            return MessageStatus.fromCode(Integer.parseInt(input));
        }
        return MessageStatus.valueOf(input);
    }

    @Override
    public @NotNull Type getType() {
        return type;
    }

    @Override
    public @NotNull Operation getOperation() {
        return operation;
    }

    @Override
    public @NotNull String getContent() {
        return content;
    }

    public @NotNull MessageStatus getStatus() {
        return status;
    }

    @Override
    public @NotNull Instant getInstant() {
        return instant;
    }

    @Override
    public @NotNull JsonObject toJson() {
        @NotNull JsonObject json = new JsonObject();
        json.addProperty("status", status.toString());
        json.addProperty("content", content);
        json.addProperty("instant", format());
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageResponse that = (MessageResponse) o;
        return Objects.equals(content, that.content) && status == that.status && Objects.equals(instant, that.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, status, instant);
    }
}
