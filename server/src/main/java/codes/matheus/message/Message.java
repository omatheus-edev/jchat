package codes.matheus.message;

import codes.matheus.entity.User;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class Message {
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

    @Override
    public String toString() {
        ZonedDateTime brazilTime = instant.atZone(ZoneId.of("America/Sao_Paulo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return brazilTime.format(formatter) + " " + sender.getUsername().getName() + ": " + content;
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
