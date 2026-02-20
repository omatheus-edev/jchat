package codes.matheus.message;

import codes.matheus.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public sealed interface Message permits MessageChat, MessageServer, MessageAuth {
    @SuppressWarnings("unchecked")
    static <T extends Message> @NotNull T create(@NotNull Type type, @Nullable User user, @NotNull String content) {
        return (T) type.create(user, content);
    }

    @NotNull Type getType();

    @NotNull String getContent();

    @NotNull Instant getInstant();

    enum Type {
        SERVER {
            @Override
            @NotNull Message create(@Nullable User user, @NotNull String content) {
                if (user != null) {
                    throw new IllegalArgumentException("user should not be provided to the server");
                }
                return new MessageServer(content);
            }
        },
        CHAT {
            @Override
            @NotNull Message create(@Nullable User user, @NotNull String content) {
                if (user == null) {
                    throw new IllegalArgumentException("user should be provided to the chat");
                }
                return new MessageChat(user, content);
            }
        },
        AUTHORIZATION {
            @Override
            @NotNull Message create(@Nullable User user, @NotNull String content) {
                if (user != null) {
                    throw new IllegalArgumentException("user should not be provided to the authorization");
                }
                return new MessageAuth(content);
            }
        };

        abstract @NotNull Message create(@Nullable User user, @NotNull String content);
    }
}
