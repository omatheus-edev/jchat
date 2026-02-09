package codes.matheus.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Password implements CharSequence {
    public static @NotNull Password create(@NotNull String password) {
        if (!validate(password)) {
            throw new IllegalArgumentException("password is invalid");
        }
        return new Password(password);
    }

    public static boolean validate(@NotNull String password) {
        if (password.isBlank()) {
            return false;
        } else if (!(password.length() >= 5 && password.length() < 15)) {
            return false;
        } else if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-z0-9#@&!_-]+$")) {
            return false;
        } else {
            return true;
        }
    }

    private @NotNull String password;

    private Password(@NotNull String password) {
        this.password = password;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        if (!validate(password)) {
            throw new IllegalArgumentException("password is invalid");
        }
        this.password = password;
    }

    @Override
    public int length() {
        return password.length();
    }

    @Override
    public char charAt(int i) {
        return password.charAt(i);
    }

    @Override
    public @NotNull CharSequence subSequence(int i, int i1) {
        return password.subSequence(i, i1);
    }

    @Override
    public @NotNull String toString() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Password password1 = (Password) o;
        return Objects.equals(password, password1.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(password);
    }
}
