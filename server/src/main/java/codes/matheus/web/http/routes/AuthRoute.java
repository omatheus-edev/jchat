package codes.matheus.web.http.routes;

import codes.matheus.entity.User;
import codes.matheus.repository.UserRepository;
import codes.matheus.util.Json;
import codes.matheus.web.http.HttpStatus;
import codes.matheus.web.http.Response;
import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class AuthRoute {
    private final @NotNull UserRepository repository;

    public AuthRoute() {
        this.repository = new UserRepository();
    }

    public void signup(@NotNull HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            Response.builder(exchange)
                    .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type")
                    .noContent()
                    .send();
            return;
        }

        if (!exchange.getRequestMethod().equals("POST")) {
            Response.builder(exchange)
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .error("method now allowed")
                    .send();
            return;
        }

        @Nullable User body = parseBody(exchange);
        if (body == null) {
            Response.builder(exchange)
                    .status(HttpStatus.BAD_REQUEST)
                    .error("invalid body")
                    .send();
            return;
        }

        if (repository.exists(body)) {
            Response.builder(exchange)
                    .status(HttpStatus.CONFLICT)
                    .error("user already exists")
                    .send();
            return;
        }

        body.generateToken();
        repository.save(body);
        Response.builder(exchange)
                .status(HttpStatus.CREATED)
                .json(body)
                .send();
    }

    public void login(@NotNull HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            Response.builder(exchange)
                    .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type")
                    .noContent()
                    .send();
            return;
        }

        if (!exchange.getRequestMethod().equals("POST")) {
            Response.builder(exchange)
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .error("method now allowed")
                    .send();
            return;
        }

        @Nullable User body = parseBody(exchange);
        if (body == null) {
            Response.builder(exchange)
                    .status(HttpStatus.BAD_REQUEST)
                    .error("invalid body")
                    .send();
            return;
        }

        @Nullable User user = repository.get(body.getUsername().getName()).orElse(null);
        if (user == null) {
            Response.builder(exchange)
                    .status(HttpStatus.NOT_FOUND)
                    .error("user not found")
                    .send();
            return;
        }

        if (!user.getPassword().getValue().equals(body.getPassword().getValue())) {
            Response.builder(exchange)
                    .status(HttpStatus.UNAUTHORIZED)
                    .error("invalid password")
                    .send();
            return;
        }

        user.generateToken();
        repository.save(user);
        Response.builder(exchange)
                .status(HttpStatus.OK)
                .json(user)
                .send();
    }

    private @Nullable User parseBody(@NotNull HttpExchange exchange) {
        try (InputStream input = exchange.getRequestBody()) {
            @NotNull String raw = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return Json.deserialize(raw);
        } catch (Exception e) {
            return null;
        }
    }
}