package codes.matheus.web.http;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),

    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    NOT_MODIFIED(304, "Not Modified"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, "Permanent Redirect"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable");

    public static @NotNull HttpStatus from(int code) {
        @NotNull HttpStatus status = map.get(code);
        if (status == null) {
            throw new IllegalArgumentException("Http code don't exists");
        }
        return status;
    }

    private static final @NotNull Map<Integer, HttpStatus> map = new HashMap<>();

    static {
        for (@NotNull HttpStatus status : values()) {
            map.put(status.code, status);
        }
    }

    private final int code;
    private final @NotNull String description;

    HttpStatus(int code, @NotNull String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code + " " + description;
    }
}