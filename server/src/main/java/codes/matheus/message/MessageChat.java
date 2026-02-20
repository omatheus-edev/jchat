package codes.matheus.message;

import codes.matheus.user.User;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class MessageChat implements Message {
    private final @NotNull Type type;
    private final @NotNull User user;
    private final @NotNull String content;
    private final @NotNull Instant instant;

    MessageChat(@NotNull User user, @NotNull String content) {
        this.type = Type.CHAT;
        this.user = user;
        this.content = content;
        this.instant = Instant.now();
    }

    @Override
    public @NotNull Type getType() {
        return type;
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
    public String toString() {
        @NotNull ZonedDateTime brazilTime = instant.atZone(ZoneId.of("America/Sao_Paulo"));
        @NotNull DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return brazilTime.format(formatter) + " " + user.getUsername() + ": " + content;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageChat that = (MessageChat) o;
        return Objects.equals(user, that.user) && Objects.equals(content, that.content) && Objects.equals(instant, that.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, content, instant);
    }
}
