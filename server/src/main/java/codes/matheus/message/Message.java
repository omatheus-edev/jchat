package codes.matheus.message;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.bind.JsonTreeWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class Message {
    public static @Nullable Message deserialize(@NotNull String json, @NotNull String sender) {
        try {
            @NotNull JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            @NotNull Type type = Type.valueOf(obj.get("type").getAsString());
            @NotNull String content = obj.get("content").getAsString();
            @NotNull Instant instant = Instant.parse(obj.get("instant").getAsString());
            return new Message(type, content, sender, instant);
        } catch (Exception e) {
            return null;
        }
    }

    private final @NotNull Type type;
    private final @NotNull String content;
    private final @NotNull String sender;
    private final @NotNull Instant instant;

    public Message(@NotNull Type type, @NotNull String content, @NotNull String sender, @NotNull Instant instant) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.instant = instant;
    }

    public Message(@NotNull Type type, @NotNull String content, @NotNull String sender) {
        this(type, content, sender, Instant.now());
    }

    public @NotNull Type getType() {
        return type;
    }

    public @NotNull String getContent() {
        return content;
    }

    public @NotNull String getSender() {
        return sender;
    }

    public @NotNull Instant getInstant() {
        return instant;
    }

    public @NotNull String serialize() {
        try (JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginObject();
            writer.name("type").value(type.name());
            writer.name("sender").value(sender);
            writer.name("content").value(content);
            writer.name("instant").value(format());
            writer.endObject();
            return writer.get().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize message: " + e.getMessage());
        }
    }

    public @NotNull String format() {
        @NotNull ZonedDateTime brazilTime = getInstant().atZone(ZoneId.of("America/Sao_Paulo"));
        @NotNull DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return brazilTime.format(formatter);
    }

    @Override
    public String toString() {
        return format() + " " + sender + ": " + content;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Message message = (Message) object;
        return Objects.equals(content, message.content) && Objects.equals(sender, message.sender) && Objects.equals(instant, message.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, sender, instant);
    }
}
