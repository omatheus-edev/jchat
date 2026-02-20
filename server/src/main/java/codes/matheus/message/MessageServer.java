package codes.matheus.message;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Objects;

public final class MessageServer implements Message {
    private final @NotNull Type type;
    private final @NotNull String content;
    private final @NotNull Instant instant;

    MessageServer(@NotNull String content) {
        this.type = Type.SERVER;
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
        MessageServer that = (MessageServer) o;
        return type == that.type && Objects.equals(content, that.content) && Objects.equals(instant, that.instant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, content, instant);
    }
}
