package codes.matheus;

import codes.matheus.message.Message;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;

public final class UI {
    public static final @NotNull String RESET = "\033[0m";
    public static final @NotNull String RED     = "\033[1;91m";
    public static final @NotNull String GREEN   = "\033[1;92m";
    public static final @NotNull String CYAN    = "\033[1;96m";
    public static final @NotNull String WHITE   = "\033[1;97m";

    public UI() {
        welcome();
    }


    public void welcome() {
        System.out.println(GREEN + "========================================" + RESET);
        System.out.println(GREEN + "          JCHAT - TERMINAL VERSION      " + RESET);
        System.out.println(GREEN + "========================================" + RESET);
    }

    public @NotNull String getValidateInput(@NotNull BufferedReader reader) throws IOException {
        while (true) {
            requirements();
            System.out.print(GREEN + "Enter [username:password]: " + RESET);
            @NotNull String input = reader.readLine().trim();

            if (input.isBlank() || !input.contains(":")) {
                System.out.println(RED + "Format invalid. username:password" + RESET);
                continue;
            }

            return input;
        }
    }

    public @NotNull Message.Operation getAuthOperation(@NotNull BufferedReader reader) throws IOException {
        System.out.println(GREEN + "\n[1] Login | [2] Sign Up" + RESET);
        System.out.print("Choose: ");
        @NotNull String choice = reader.readLine();
        return "2".equals(choice) ? Message.Operation.AUTH_SIGNUP : Message.Operation.AUTH_LOGIN;
    }

    private void requirements() {
        System.out.println("\n" + WHITE + "Access rules:" + RESET);
        System.out.println(CYAN + "  Username" + RESET);
        System.out.println("    ✔ 3 a 14 characters | ✔ Letters and numbers | ✔ Not just numbers");
        System.out.println(CYAN + "  Password" + RESET);
        System.out.println("    ✔ 5 a 14 characters | ✔ At least one letter and one number");
        System.out.println("------------------------------------------------------------");
    }
}
