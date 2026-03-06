package codes.matheus.web.http.routes;

import codes.matheus.web.http.HttpStatus;
import codes.matheus.web.http.Response;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public final class ChatRoute implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        @Nullable InputStream html = getClass().getResourceAsStream("/frontend/index.html");

        if (html == null) {
            Response.builder(exchange)
                    .status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .error("index.html not found")
                    .send();
            return;
        }

        byte[] bytes = html.readAllBytes();
        Response.builder(exchange)
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(new String(bytes))
                .send();

        html.close();
    }
}
