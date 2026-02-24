package codes.matheus.server;

import codes.matheus.entity.Account;
import codes.matheus.entity.Client;
import codes.matheus.message.Message;
import codes.matheus.message.MessageChat;
import codes.matheus.message.Protocol;
import codes.matheus.user.Password;
import codes.matheus.user.User;
import codes.matheus.user.Username;
import org.jetbrains.annotations.NotNull;

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
    private final @NotNull Broadcast broadcast;
    private final @NotNull Protocol protocol;
    private final @NotNull Set<Client> clients = ConcurrentHashMap.newKeySet();

    public WorkerThread(@NotNull Selector selector) {
        this.executor = Executors.newFixedThreadPool(2);
        this.selector = selector;
        this.broadcast = new Broadcast(clients);
        this.protocol = new Protocol();
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

                        @NotNull Message response = Message.create(Message.Type.RESPONSE, Message.Operation.AUTH, "201|Authorization completed successfully");
                        broadcast.toUser(parts[0], response);

                        @NotNull Message message = Message.create(Message.Type.CHAT, Message.Operation.BROADCAST, "SERVER:" + parts[0] + " joined the chat");
                        broadcast.toOthers(client, message);
                        Server.log.info(parts[0] + " joined");
                    } else {
                        broadcast.toUser(socket, Message.create(Message.Type.RESPONSE, Message.Operation.AUTH, "401|Invalid data"));
                        socket.close();
                        key.cancel();
                    }
                } else {
                    @NotNull Client sender = (Client) key.attachment();
                    @NotNull MessageChat received = protocol.decode(text);
                    switch (received.getOperation()) {
                        case BROADCAST -> broadcast.toOthers(sender, received);
                        case PRIVATE_MESSAGE -> {
                            @NotNull String[] parts = received.getContent().split(":");
                            if (parts.length == 2) {
                                broadcast.toUser(parts[0], received);
                            }
                        }
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
}
