package codes.matheus.user;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Username implements CharSequence {
    public static @NotNull Username create(@NotNull String name) {
        if (!validate(name)) {
            throw new IllegalArgumentException("username is invalid");
        }
        return new Username(name);
    }

    public static boolean validate(@NotNull String name) {
        if (name.isBlank()) {
            return false;
        } else if (name.matches("\\d+")) {
            return false;
        } else if (!(name.length() >= 3 && name.length() < 15)) {
            return false;
        } else if (!name.matches("^[a-zA-Z0-9_.#@!-]+$")) {
            return false;
        } else {
            return true;
        }
    }

    private @NotNull String name;

    private Username(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public char charAt(int i) {
        return name.charAt(i);
    }

    @Override
    public @NotNull CharSequence subSequence(int i, int i1) {
        return name.subSequence(i, i1);
    }

    @Override
    public @NotNull String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Username username = (Username) o;
        return Objects.equals(name, username.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
