package codes.matheus.message;

import codes.matheus.user.Password;
import codes.matheus.user.Username;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public final class MessageAuth implements Message {
    public static boolean validate(@NotNull String content) {
        if (!content.contains(":")) return false;
        @NotNull String[] parts = content.split(":");
        if (parts.length != 2) return false;

        return Username.validate(parts[0]) && Password.validate(parts[1]);
    }

    private final @NotNull Type type;
    private final @NotNull String content;
    private final @NotNull Instant instant;

    MessageAuth(@NotNull String content) {
        if (!validate(content)) {
            throw new IllegalArgumentException("invalid user data");
        }
        this.type = Type.AUTHORIZATION;
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MessageAuth that = (MessageAuth) o;
        return type == that.type && Objects.equals(content, that.content) && Objects.equals(instant, that.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, content, instant);
    }
}
