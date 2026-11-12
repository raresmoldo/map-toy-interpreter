package interpreter.view;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class TextMenu {
    private final Map<String, Command> commands = new LinkedHashMap<>(); // in order
    private final Scanner scanner;
    private final PrintStream out;

    public TextMenu() {
        this(System.in, System.out);
    }

    public TextMenu(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.out = output;
    }

    public void addCommand(Command c) { commands.put(c.getKey(), c); }

    private void printMenu() {
        for (Command c : commands.values())
            out.printf("%4s : %s%n", c.getKey(), c.getDescription());
    }

    public void show() {
        while (true) {
            printMenu();
            out.print("Input the option: ");
            out.flush();
            if (!scanner.hasNextLine()) return; // end of input
            Command c = commands.get(scanner.nextLine().strip());
            if (c == null) {
                out.println("Invalid option");
                continue;
            }
            c.execute();
        }
    }
}