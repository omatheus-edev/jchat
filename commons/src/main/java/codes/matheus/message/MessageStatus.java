package codes.matheus.message;

import org.jetbrains.annotations.NotNull;

public enum MessageStatus {
    OK(200),
    CREATED(201),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    public static @NotNull MessageStatus fromCode(int code) {
        for (@NotNull MessageStatus status : values()) {
            if (status.code == code) return status;
        }
        return INTERNAL_SERVER_ERROR;
    }

    private final int code;

    MessageStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
