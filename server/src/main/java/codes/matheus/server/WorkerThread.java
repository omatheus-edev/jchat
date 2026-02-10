package codes.matheus.server;

import codes.matheus.entity.Account;
import codes.matheus.entity.Client;
import codes.matheus.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WorkerThread {
    private final @NotNull ExecutorService executor;
    private final @NotNull Selector selector;
    private final @NotNull Set<Client> clients;

    public WorkerThread(@NotNull Selector selector, @NotNull Set<Client> clients) {
        this.executor = Executors.newFixedThreadPool(2);
        this.selector = selector;
        this.clients = clients;
    }

    public void submit(@NotNull SelectionKey key) {
        executor.execute(() -> {
            try {
                @NotNull SocketChannel socket = (SocketChannel) key.channel();
                if (key.attachment() == null) {
                    @NotNull ByteBuffer buffer = ByteBuffer.allocate(1024);
                    if (socket.read(buffer) > 0) {
                        buffer.flip();
                        @NotNull String[] parts = new String(buffer.array()).trim().split(":");
                        @NotNull Client client = new Client(new Account(new User(parts[0], parts[1])), socket);
                        key.attach(client);
                        clients.add(client);
                        Server.log.info(parts[0] + " joined on chat");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (key.isValid()) {
                key.interestOps(SelectionKey.OP_READ);
                selector.wakeup();
            }
        });
    }
}
