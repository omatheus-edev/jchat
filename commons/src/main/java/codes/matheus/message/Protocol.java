package codes.matheus.message;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class Protocol {
    public @NotNull String encode(@NotNull Message message) {
        return message.getType() + "\r\n" +
                message.getOperation() + "\r\n\r\n" +
                message.toJson();
    }

    public <T extends Message> @NotNull T decode(@NotNull String message) {
        @NotNull String[] parts = message.split("\r\n\r\n", 2);
        @NotNull String header = parts[0];
        @NotNull String body = parts[1];
        @NotNull String[] headerParts = header.split("\r\n");
        @NotNull Message.Type type = Message.Type.valueOf(headerParts[0]);
        @NotNull Message.Operation operation = Message.Operation.valueOf(headerParts[1]);
        @NotNull JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        @NotNull Instant instant = parse(json.get("instant").getAsString());
        @NotNull String content = json.get("content").getAsString();

        return switch (type) {
            case REQUEST -> Message.create(type, operation, content, instant);
            case RESPONSE -> {
                @NotNull MessageStatus status = MessageStatus.fromCode(json.get("status").getAsInt());
                yield Message.create(type, operation, status + "|" + content, instant);
            }
            case CHAT -> {
                @NotNull String sender = json.get("sender").getAsString();
                yield Message.create(type, operation, sender + ":" + content, instant);
            }
        };

    }

    private @NotNull Instant parse(@NotNull String date) {
        @NotNull LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return dateTime.atZone(ZoneId.of("America/Sao_Paulo")).toInstant();
    }
}
