package codes.matheus.repository;

import codes.matheus.entity.User;
import codes.matheus.web.http.Server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class UserRepository {
    private final @NotNull String path = "users.json";
    private final @NotNull Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private final @NotNull Set<User> users = ConcurrentHashMap.newKeySet();

    public UserRepository() {
        load();
    }

    private void load() {
        @NotNull File file = new File(path);
        if (!file.exists()) return;

        try (Reader reader = new FileReader(file)) {
            @NotNull JsonArray array = gson.fromJson(reader, JsonArray.class);

            if (array != null) {
                array.forEach(element -> {
                    @NotNull JsonObject object = element.getAsJsonObject();
                    @NotNull String username = object.get("username").getAsString();
                    @NotNull String password = object.get("password").getAsString();
                    users.add(new User(username, password));
                });
            }

        } catch (IOException e) {
            Server.log.severe("Failed to load users: " + e.getMessage());
        }
    }

    public synchronized void save(@NotNull User user) {
        users.add(user);
        @NotNull JsonArray array = new JsonArray();
        users.forEach(u -> {
            @NotNull JsonObject object =  new JsonObject();
            object.addProperty("username", u.getUsername().getName());
            object.addProperty("password", u.getPassword().getValue());
            array.add(object);
        });

        try (Writer writer = new FileWriter(path)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            Server.log.severe("Failed to load users: " + e.getMessage());
        }
    }

    public @NotNull Optional<User> get(@NotNull String username) {
        return users.stream().filter(u -> u.getUsername().getName().equals(username)).findFirst();
    }

    public boolean exists(@NotNull User user) {
        return users.contains(user);
    }
}
