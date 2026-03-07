package codes.matheus.web.websocket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebSocketHandshake {
    private final @NotNull Pattern KEY_PATTERN = Pattern.compile("Sec-WebSocket-Key: (.+)\\r\\n");

    public boolean isHandshake(@NotNull String data) {
        return data.startsWith("GET ") && data.contains("Upgrade: websocket");
    }

    public @Nullable String extractToken(@NotNull String request) {
        Pattern pattern = Pattern.compile("GET /.*[?&]token=([^& \\r\\n]+)");
        Matcher matcher = pattern.matcher(request);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public @NotNull String response(@NotNull String request) {
        @NotNull Matcher matcher = KEY_PATTERN.matcher(request);
        if (!matcher.find()) throw new IllegalArgumentException("No WebSocket key found");

        @NotNull String key = matcher.group(1).trim();
        @NotNull String accept = generateAccept(key);

        return "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + accept + "\r\n\r\n";
    }

    private @NotNull String generateAccept(@NotNull String key) {
        try {
            @NotNull String combined = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            @NotNull MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
