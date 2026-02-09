package codes.matheus.entity;

import codes.matheus.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Account {
    private final @NotNull User user;
    private @Nullable Client client;

    public Account(@NotNull User user) {
        this.user = user;
    }

    public @NotNull User getUser() {
        return user;
    }

    public @Nullable Client getClient() {
        return client;
    }

    public void setClient(@Nullable Client client) {
        this.client = client;
    }
}
