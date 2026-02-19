package codes.matheus.server;

import com.jlogm.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public final class Server {
    public static final @NotNull Logger log = Logger.create(Server.class);
    private final @NotNull ServerSocketChannel server;
    private final @NotNull Selector selector;
    private final @NotNull WorkerThread thread;

    public Server() {
        try {
            this.server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress("localhost", 8080));
            server.configureBlocking(false);
            this.selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            this.thread = new WorkerThread(selector);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            log.info("Server running on address: " + server.getLocalAddress());
            while (server.isOpen() && selector.isOpen()) {
                @NotNull Iterator<SelectionKey> keyIterator;
                selector.select();
                keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    @NotNull SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        @Nullable SocketChannel socket = server.accept();
                        if (socket != null) {
                            socket.configureBlocking(false);
                            socket.register(selector, SelectionKey.OP_READ);
                        }
                    }

                    if (key.isReadable()) {
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);

                        @NotNull ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int read = ((SocketChannel) key.channel()).read(buffer);
                        if (read > 0) {
                            buffer.flip();
                            byte[] data = new byte[buffer.remaining()];
                            buffer.get(data);
                            thread.submit(key, data);
                        } else if (read == -1) {
                            key.channel().close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
