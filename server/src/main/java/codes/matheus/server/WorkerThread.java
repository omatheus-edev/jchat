package codes.matheus.server;

import codes.matheus.entity.Account;
import codes.matheus.entity.Client;
import codes.matheus.user.Password;
import codes.matheus.user.User;
import codes.matheus.user.Username;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WorkerThread {
    private final @NotNull ExecutorService executor;
    private final @NotNull Selector selector;
    private final @NotNull Set<Client> clients = ConcurrentHashMap.newKeySet();

    public WorkerThread(@NotNull Selector selector) {
        this.executor = Executors.newFixedThreadPool(2);
        this.selector = selector;
    }

    public void submit(@NotNull SelectionKey key, byte[] data) {
        executor.execute(() -> {
            try {
                @NotNull String text = new String(data).trim();
                if (key.attachment() == null) {
                    @NotNull String[] parts = text.split(":");
                    if (parts.length < 2) return;

                    @NotNull SocketChannel socket = (SocketChannel) key.channel();
                    if (Username.validate(parts[0]) && Password.validate(parts[1])) {
                        @NotNull Client client = new Client(new Account(new User(parts[0], parts[1])), socket);
                        key.attach(client);
                        client.getAccount().setClient(client);
                        clients.add(client);
                        client.write("AUTH_SUCCESS");
                        Server.log.info(parts[0] + " joined");
                        broadcast(client.getAccount().getUser().getUsername() + " joined the chat");
                    } else {
                        socket.write(ByteBuffer.wrap("AUTH_FAILED".getBytes()));
                        socket.close();
                        key.cancel();
                    }
                } else {
                    @NotNull Client sender = (Client) key.attachment();
                    @NotNull String message = new String(data).trim();
                    if (!message.isBlank()) {
                        System.out.println(message);
                        broadcast(message, sender);
                    }
                }
            } catch (Exception e) {
                Server.log.severe("Error processing worker task: " + e.getMessage());
            }

            if (key.isValid()) {
                key.interestOps(SelectionKey.OP_READ);
                selector.wakeup();
            }
        });
    }

    private void broadcast(@NotNull String message, @NotNull Client sender) throws IOException {
        for (@NotNull Client client : clients) {
            if (sender != client) {
                client.write(message);
            }
        }
    }

    public void broadcast(@NotNull String message) throws IOException {
        for (@NotNull Client client : clients) {
            client.write(message);
        }
    }
}
