package codes.matheus.server;

import codes.matheus.entity.Account;
import codes.matheus.entity.Client;
import codes.matheus.message.Message;
import codes.matheus.message.MessageChat;
import codes.matheus.message.Protocol;
import codes.matheus.repository.UserRepository;
import codes.matheus.user.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WorkerThread {
    private final @NotNull ExecutorService executor;
    private final @NotNull Selector selector;
    private final @NotNull Broadcast broadcast;
    private final @NotNull Protocol protocol;
    private final @NotNull UserRepository repository;
    private final @NotNull Set<Client> clients = ConcurrentHashMap.newKeySet();

    public WorkerThread(@NotNull Selector selector) {
        this.executor = Executors.newFixedThreadPool(2);
        this.selector = selector;
        this.broadcast = new Broadcast(clients);
        this.repository = new UserRepository();
        this.protocol = new Protocol();
    }

    public void submit(@NotNull SelectionKey key, byte[] data) {
        executor.execute(() -> {
            try {
                @NotNull String text = new String(data).trim();
                if (key.attachment() == null) {
                    @NotNull Message message = protocol.decode(text);

                    switch (message.getOperation()) {
                        case AUTH_SIGNUP -> signup(key, message);
                        case AUTH_LOGIN -> login(key, message);
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
                if (key.attachment() instanceof Client client) {
                    clients.remove(client);
                    Server.log.info("Client " + client.getAccount().getUser().getUsername().getName() + " disconnected due to error.");
                }
                try {
                    key.channel().close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                key.cancel();
            }

            if (key.isValid()) {
                key.interestOps(SelectionKey.OP_READ);
                selector.wakeup();
            }
        });
    }

    private void signup(@NotNull SelectionKey key, @NotNull Message message) {
        @NotNull String[] parts = message.getContent().split(":");
        if (parts.length < 2) return;

        @NotNull SocketChannel socket = (SocketChannel) key.channel();
        @NotNull User user = new User(parts[0], parts[1]);
        if (repository.exists(user)) {
            broadcast.toUser(socket, Message.create(Message.Type.RESPONSE, Message.Operation.AUTH_SIGNUP, "401|User already exists"));
            return;
        }

        repository.save(user);
        authenticate(key, socket, user);
    }

    private void login(@NotNull SelectionKey key, @NotNull Message message) {
        @NotNull String[] parts = message.getContent().split(":");
        if (parts.length < 2) return;

        @NotNull SocketChannel socket = (SocketChannel) key.channel();
        @NotNull Optional<User> optionalUser = repository.get(parts[0]);
        if (optionalUser.isEmpty()) {
            broadcast.toUser(socket, Message.create(Message.Type.RESPONSE, Message.Operation.AUTH_LOGIN, "404|User not found"));
            return;
        }

        @NotNull User user = optionalUser.get();
        if (!user.getPassword().getValue().equals(parts[1])) {
            broadcast.toUser(socket,
                    Message.create(Message.Type.RESPONSE, Message.Operation.AUTH_LOGIN, "401|Invalid password"));
            return;
        }
        authenticate(key, socket, user);
    }

    private void authenticate(@NotNull SelectionKey key, @NotNull SocketChannel socket, @NotNull User user) {
        @NotNull Client client = new Client(new Account(user), socket);
        key.attach(client);
        client.getAccount().setClient(client);
        clients.add(client);

        @NotNull Message response = Message.create(Message.Type.RESPONSE, Message.Operation.AUTH_LOGIN, "201|Authorization completed successfully");
        broadcast.toUser(user.getUsername().getName(), response);
        @NotNull Message message = Message.create(Message.Type.CHAT, Message.Operation.BROADCAST, "SERVER:" + user.getUsername().getName() + " joined the chat");
        broadcast.toOthers(client, message);
        Server.log.info(user.getUsername().getName() + " joined");
    }
}
