package codes.matheus.server;

import codes.matheus.entity.Client;
import com.jlogm.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Server {
    public static final @NotNull Logger log = Logger.create(Server.class);
    private final @NotNull ServerSocketChannel server;
    private final @NotNull Selector selector;
    private final @NotNull WorkerThread thread;
    private final @NotNull Set<Client> clients = ConcurrentHashMap.newKeySet();

    public Server() {
        try {
            this.server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress("localhost", 8080));
            server.configureBlocking(false);
            this.selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            this.thread = new WorkerThread(selector, clients);
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
                            log.info("New client joined on server");
                            socket.register(selector, SelectionKey.OP_READ);
                        }
                    }

                    if (key.isReadable()) {
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                        thread.submit(key);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
