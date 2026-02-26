package codes.matheus;

import codes.matheus.message.Message;
import codes.matheus.message.MessageChat;
import codes.matheus.message.MessageResponse;
import codes.matheus.message.Protocol;
import codes.matheus.user.Username;
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
import java.util.Iterator;

public final class Client {
    public static final @NotNull Logger log = Logger.create(Client.class);
    private final @NotNull SocketChannel socket;
    private final @NotNull Selector selector;
    private final @NotNull Protocol protocol;
    private volatile boolean authenticated = false;

    public Client() {
        try {
            this.socket = SocketChannel.open(new InetSocketAddress("0.0.0.0", 8080));
            socket.configureBlocking(false);
            this.selector = Selector.open();
            socket.register(selector, SelectionKey.OP_READ);
            this.protocol = new Protocol();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() throws IOException {
        @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        @NotNull UI ui = new UI();
        @NotNull String data = ui.getValidateInput(reader);
        @NotNull String username = data.split(":")[0];
        socket.write(ByteBuffer.wrap(data.getBytes()));
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
                            @NotNull ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int read = socket.read(buffer);
                            if (read > 0) {
                                buffer.flip();
                                @NotNull Message message = protocol.decode(new String(buffer.array(),0, buffer.limit()));

                                if (message instanceof MessageResponse response) {
                                    if (response.getStatus().getCode() == 201) {
                                        log.info("Successfully authenticated");
                                        authenticated = true;
                                    } else if (response.getStatus().getCode() == 401) {
                                        log.severe("Connection failed: Invalid credentials");
                                        socket.close();
                                        return;
                                    }
                                } else if(message instanceof MessageChat chat) {
                                    if (chat.getOperation().equals(Message.Operation.PRIVATE_MESSAGE)) {
                                        System.out.println(chat.format() + " \033[1;91m[PRIVATE MESSAGE]\033[0m " + chat.getSender() + ": " + chat.getContent());
                                    } else {
                                        System.out.println(chat);
                                    }
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
                            if (text.equalsIgnoreCase("exit")) {
                                socket.close();
                                break;
                            }

                            @NotNull Message.Operation operation = Message.Operation.BROADCAST;
                            if (text.contains(":")) {
                                @NotNull String[] parts = text.split(":");
                                if (Username.validate(parts[0])) {
                                    operation = Message.Operation.PRIVATE_MESSAGE;
                                }
                            }

                            @NotNull Message message = Message.create(Message.Type.CHAT, operation,username + ":" + text);
                            socket.write(ByteBuffer.wrap(protocol.encode(message).getBytes()));
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
