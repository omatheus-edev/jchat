package codes.matheus.mapper;

import codes.matheus.message.Message;
import codes.matheus.user.User;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.JsonTreeWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class JsonMapper {
    public @NotNull JsonObject toJson(@NotNull User user) {
        try (JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginObject();
            writer.name("name").value(user.getUsername().getName());
            writer.name("password").value(user.getPassword().getPassword());
            writer.endObject();
            return writer.get().getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException("failed to serialize " + e.getMessage());
        }
    }

    public @NotNull JsonObject toJson(@NotNull Message message) {
        try (JsonTreeWriter writer = new JsonTreeWriter()) {
            @NotNull String[] parts = message.getContent().split(":");
            writer.beginObject();
            writer.name("user").value(parts[0]);
            writer.name("message").value(parts[1]);
            writer.name("instant").value(message.getInstant().toString());
            writer.endObject();
            return writer.get().getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException("failed to serialize " + e.getMessage());
        }
    }
}
