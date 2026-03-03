package codes.matheus.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class WebSocketFrame {
    private WebSocketFrame() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

    public static @NotNull ByteBuffer encode(@NotNull String text) {
        byte[] payload = text.getBytes(StandardCharsets.UTF_8);
        int length = payload.length;

        @NotNull ByteBuffer frame;
        if (length <= 125) {
            frame = ByteBuffer.allocate(2 + length);
            frame.put((byte) 0x81);
            frame.put((byte) length);
        } else if (length <= 65535) {
            frame = ByteBuffer.allocate(4 + length);
            frame.put((byte) 0x81);
            frame.put((byte) 126);
            frame.put((byte) ((length >> 8) & 0xFF));
            frame.put((byte) (length & 0xFF));
        } else {
            frame = ByteBuffer.allocate(10 + length);
            frame.put((byte) 0x81);
            frame.put((byte) 127);
            frame.putLong(length);
        }

        frame.put(payload);
        frame.flip();
        return frame;
    }

    public static @Nullable String decode(@NotNull ByteBuffer buffer) {
        if (buffer.remaining() < 2) return null;
        byte b1 = buffer.get();
        byte b2 = buffer.get();
        int opcode = b1 & 0x0F;

        if (opcode == 0x8) return null;
        if (opcode == 0x9) return "";
        if (opcode != 0x1) return "";

        boolean masked = (b2 & 0x80) != 0;
        int length = b2 & 0x7F;

        if (length == 126) {
            if (buffer.remaining() < 2) return null;
            length = ((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
        } else if (length == 127) {
            if (buffer.remaining() < 8) return null;
            buffer.getInt();
            length = buffer.getInt();
        }

        byte[] mask = new byte[4];
        if (masked) {
            if (buffer.remaining() < 4) return null;
            buffer.get(mask);
        }

        if (buffer.remaining() < length) return null;

        byte[] payload = new byte[length];
        buffer.get(payload);

        if (masked) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] ^= mask[i % 4];
            }
        }

        return new String(payload, java.nio.charset.StandardCharsets.UTF_8);
    }
}
