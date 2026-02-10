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
        log.info("Connected to the server");
        new Thread(() -> {
            try {
                while (socket.isOpen() && selector.isOpen()) {
                    @NotNull Iterator<SelectionKey> keyIterator;
                    selector.select();
                    keyIterator = selector.selectedKeys().iterator();

                    if (keyIterator.hasNext()) {
                        @NotNull SelectionKey key = keyIterator.next();
                        keyIterator.remove();


                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
