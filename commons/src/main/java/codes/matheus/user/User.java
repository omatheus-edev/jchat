package codes.matheus.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class User {
    private final @NotNull Username username;
    private final @NotNull Password password;

    public User(@NotNull String username, @NotNull String password) {
        this.username = Username.create(username);
        this.password = Password.create(password);
    }

    public @NotNull Username getUsername() {
        return username;
    }

    public @NotNull Password getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password.setPassword(password);
    }

    public void setUsername(@NotNull String username) {
        this.username.setName(username);
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
