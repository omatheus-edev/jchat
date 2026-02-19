package codes.matheus;

import com.jlogm.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public final class Client {
    public static final @NotNull Logger log = Logger.create(Client.class);
    private final @NotNull SocketChannel socket;
    private final @NotNull Selector selector;
    private volatile boolean authenticated = false;

    public Client() {
        try {
            this.socket = SocketChannel.open(new InetSocketAddress("localhost", 8080));
            socket.configureBlocking(false);
            this.selector = Selector.open();
            socket.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() throws IOException {
        @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter your data: ");
        @NotNull String data = reader.readLine();
        @NotNull ByteBuffer buffer = ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));

        socket.write(buffer);
        log.info("Data sent, waiting for server approval...");
        new Thread(() -> {
            try {
                while (socket.isOpen() && selector.isOpen()) {
                    @NotNull Iterator<SelectionKey> keyIterator;
                    selector.select();
                    keyIterator = selector.selectedKeys().iterator();

                    if (keyIterator.hasNext()) {
                        @NotNull SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isReadable()) {
                            @NotNull ByteBuffer response = ByteBuffer.allocate(1024);
                            int read = socket.read(response);
                            if (read > 0) {
                                response.flip();
                                @NotNull String message = new String(response.array(), 0, response.limit());

                                if (message.contains("AUTH_SUCCESS")) {
                                    log.info("Successfully authenticated");
                                    authenticated = true;
                                } else if (message.contains("AUTH_FAILED")) {
                                    log.severe("Connection failed: Invalid credentials");
                                    socket.close();
                                } else {
                                    System.out.println(message);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                while (socket.isOpen() && selector.isOpen()) {
                    if (authenticated) {
                        @NotNull String text = reader.readLine();

                        if (!text.isBlank()) {
                            socket.write(ByteBuffer.wrap(text.getBytes()));
                            if (text.equalsIgnoreCase("exit")) {
                                socket.close();
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
