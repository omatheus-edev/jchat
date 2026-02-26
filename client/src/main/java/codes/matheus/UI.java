package codes.matheus;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;

public final class UI {
    public static final @NotNull String RESET = "\033[0m";
    public static final @NotNull String RED     = "\033[1;91m";
    public static final @NotNull String GREEN   = "\033[1;92m";
    public static final @NotNull String CYAN    = "\033[1;96m";
    public static final @NotNull String WHITE   = "\033[1;97m";

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

    private void requirements() {
        System.out.println("\n" + WHITE + "Access rules:" + RESET);
        System.out.println(CYAN + "  Username" + RESET);
        System.out.println("    ✔ 3 a 14 characters | ✔ Letters and numbers | ✔ Not just numbers");
        System.out.println(CYAN + "  Password" + RESET);
        System.out.println("    ✔ 5 a 14 characters | ✔ At least one letter and one number");
        System.out.println("------------------------------------------------------------");
    }
}
