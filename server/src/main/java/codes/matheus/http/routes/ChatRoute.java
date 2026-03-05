package codes.matheus.http.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ChatRoute implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        @Nullable InputStream html = getClass().getResourceAsStream("/frontend/index.html");

        if (html == null) {
            byte[] body = "404 - index.html not found".getBytes();
            exchange.sendResponseHeaders(404, body.length);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(body);
            }
            return;
        }

        byte[] bytes = html.readAllBytes();
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
