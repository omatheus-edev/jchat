package codes.matheus.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class User {
    private final @NotNull Username username;
    private final @NotNull Password password;
    private @Nullable String token;

    public User(@NotNull String username, @NotNull String password) {
        this.username = Username.create(username);
        this.password = Password.create(password);
        this.token = null;
    }

    public @NotNull Username getUsername() {
        return username;
    }

    public @NotNull Password getPassword() {
        return password;
    }

    public @Nullable String getToken() {
        return token;
    }

    public void setPassword(@NotNull String password) {
        this.password.setPassword(password);
    }

    public void setUsername(@NotNull String username) {
        this.username.setName(username);
    }

    public void generateToken() {
        this.token = UUID.randomUUID().toString();
    }

    public void invalidateToken() {
        this.token = null;
    }

    @Override
    public String toString() {
        return "User{" +
                "username=" + username +
                ", password=" + password +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
