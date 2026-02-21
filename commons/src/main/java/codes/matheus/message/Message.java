package codes.matheus.message;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public sealed interface Message permits MessageChat, MessageRequest, MessageResponse {
    @SuppressWarnings("unchecked")
    static <T extends Message> @NotNull T create(@NotNull Type type, @NotNull Operation operation, @NotNull String content) {
        return (T) type.create(operation, content, Instant.now());
    }

    @SuppressWarnings("unchecked")
    static <T extends Message> @NotNull T create(@NotNull Type type, @NotNull Operation operation, @NotNull String content, @NotNull Instant instant) {
        return (T) type.create(operation, content, instant);
    }

    @NotNull Type getType();

    @NotNull Operation getOperation();

    @NotNull String getContent();

    @NotNull Instant getInstant();

    @NotNull JsonObject toJson();

    default @NotNull String format() {
        @NotNull ZonedDateTime brazilTime = getInstant().atZone(ZoneId.of("America/Sao_Paulo"));
        @NotNull DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return brazilTime.format(formatter);
    }

    enum Type {
        REQUEST(Operation.AUTH) {
            @NotNull Message create(@NotNull Operation operation, @NotNull String content, @NotNull Instant instant) {
                if (!support(operation)) {
                    throw new RuntimeException("chat request should not receive this operation");
                }
                return new MessageRequest(operation, content, instant);
            }
        },
        RESPONSE(Operation.AUTH) {
            @NotNull Message create(@NotNull Operation operation, @NotNull String content, @NotNull Instant instant) {
                if (!support(operation)) {
                    throw new RuntimeException("chat response should not receive this operation");
                }
                return new MessageResponse(operation, content, instant);
            }
        },
        CHAT(Operation.BROADCAST, Operation.PRIVATE_MESSAGE) {
            @NotNull Message create(@NotNull Operation operation, @NotNull String content, @NotNull Instant instant) {
                if (!support(operation)) {
                    throw new RuntimeException("chat message should not receive this operation");
                }
                return new MessageChat(operation, content, instant);
            }
        };

        private final @NotNull List<Operation> allowedOperations;

        Type(@NotNull Operation... operations) {
            this.allowedOperations = List.of(operations);
        }

        public boolean support(@NotNull Operation operation) {
            return allowedOperations.contains(operation);
        }

        abstract @NotNull Message create(@NotNull Operation operation, @NotNull String content, @NotNull Instant instant);
    }

    enum Operation {
        AUTH,
        BROADCAST,
        PRIVATE_MESSAGE
    }
}
