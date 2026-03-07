package codes.matheus.web.websocket;

import codes.matheus.chat.ChatHandler;
import codes.matheus.chat.ChatRoom;
import codes.matheus.entity.User;
import codes.matheus.repository.UserRepository;
import codes.matheus.web.http.Server;
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
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public final class WebSocketServer {
    public static final @NotNull Logger log = Logger.create(WebSocketServer.class);
    private final int port;
    private final @NotNull ChatRoom chatRoom;
    private final @NotNull ChatHandler chatHandler;
    private final @NotNull WebSocketHandshake handshake;
    private final @NotNull UserRepository repository;
    private final @NotNull ServerSocketChannel server;
    private final @NotNull Selector selector;

    public WebSocketServer(int port) {
        try {
            this.server = ServerSocketChannel.open();
            this.selector = Selector.open();
            this.port = port;
            this.chatRoom = new ChatRoom();
            this.handshake = new WebSocketHandshake();
            this.repository = UserRepository.getInstance();
            this.chatHandler = new ChatHandler(chatRoom);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        try {
            server.bind(new InetSocketAddress("0.0.0.0", port));
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);

            log.info("Web Socket Server running on address: " + server.getLocalAddress());
            while (server.isOpen() && selector.isOpen()) {
                @NotNull Iterator<SelectionKey> keyIterator;
                selector.select();
                keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    @NotNull SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        accept();
                    }

                    if (key.isReadable()) {
                        read(key);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void accept() throws IOException {
        @Nullable SocketChannel socket = server.accept();
        if (socket != null) {
            socket.configureBlocking(false);
            socket.register(selector, SelectionKey.OP_READ);
            log.info("New connection accepted");
        }
    }

    private void read(@NotNull SelectionKey key) throws IOException {
        @NotNull SocketChannel socket = (SocketChannel) key.channel();
        @NotNull ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = socket.read(buffer);

        if (read == -1) { disconnect(key); return; }
        if (read == 0) return;

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        String text = new String(data).trim();

        if (!(key.attachment() instanceof @NotNull WebSocketSession session)) {
            if (!handshake.isHandshake(text)) { disconnect(key); return; }

            @Nullable String token = handshake.extractToken(text);
            if (token == null) { disconnect(key); return; }

            @Nullable User user = repository.getByToken(token);
            if (user == null) { disconnect(key); return; }

            socket.write(ByteBuffer.wrap(handshake.response(text).getBytes(StandardCharsets.UTF_8)));

            @NotNull WebSocketSession session = new WebSocketSession(socket, user);
            key.attach(session);
            chatRoom.join(session);
            reactivate(key);
            return;
        }

        @Nullable String decoded = WebSocketFrame.decode(ByteBuffer.wrap(data));
        if (decoded == null)  { disconnect(key); return; }
        if (decoded.isEmpty()) { reactivate(key); return; }

        chatHandler.handle(session, decoded);
        reactivate(key);
    }

    private void disconnect(@NotNull SelectionKey key) {
        if (key.attachment() instanceof WebSocketSession session) {
            session.getUser().invalidateToken();
            repository.save(session.getUser());
            chatRoom.leave(session);
        }
        try {
            key.channel().close();
        } catch (IOException e) {
            Server.log.severe(e.getMessage());
        }
        key.cancel();
    }

    private void reactivate(@NotNull SelectionKey key) {
        if (key.isValid()) {
            key.interestOps(SelectionKey.OP_READ);
            selector.wakeup();
        }
    }
}