package codes.matheus;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        @NotNull Client client = new Client();
        client.connect();
    }
}