package codes.matheus.util;

import codes.matheus.entity.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.bind.JsonTreeWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class Json {
    private Json() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

    public static @NotNull String serialize(@NotNull String key, @NotNull String value) {
        try (@NotNull JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginObject();
            writer.name(key).value(value);
            writer.endObject();
            return writer.get().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize: " + e.getMessage());
        }
    }

    public static @NotNull String serialize(@NotNull User user) {
        try (JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginObject();
            writer.name("name").value(user.getUsername().getName());
            writer.name("token").value(user.getToken());
            writer.endObject();
            return writer.get().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize: " + e.getMessage());
        }
    }

    public static @NotNull User deserialize(@NotNull String json) {
        @NotNull JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        @NotNull String username = object.get("name").getAsString();
        @NotNull String password = object.get("password").getAsString();
        return new User(username, password);
    }
}
