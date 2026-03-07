package codes.matheus.message;

import codes.matheus.entity.User;
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
    public static @Nullable Message deserialize(@NotNull String json, @NotNull User sender) {
        try {
            @NotNull JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            @NotNull Type type = Type.valueOf(obj.get("type").getAsString());
            @NotNull String content = obj.get("content").getAsString();
            return new Message(type, content, sender);
        } catch (Exception e) {
            return null;
        }
    }

    private final @NotNull Type type;
    private final @NotNull String content;
    private final @NotNull User sender;
    private final @NotNull Instant instant;

    public Message(@NotNull Type type, @NotNull String content, @NotNull User sender) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.instant = Instant.now();
    }

    public @NotNull Type getType() {
        return type;
    }

    public @NotNull String getContent() {
        return content;
    }

    public @NotNull User getSender() {
        return sender;
    }

    public @NotNull Instant getInstant() {
        return instant;
    }

    public @NotNull String serialize() {
        try (JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginObject();
            writer.name("type").value(type.name());
            writer.name("sender").value(sender.getUsername().getName());
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
        return format() + " " + sender.getUsername().getName() + ": " + content;
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
