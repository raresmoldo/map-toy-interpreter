package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.repository.Repository;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class TextMenu {
    private final Scanner in;
    private final PrintStream out;
    private final List<IStmt> programs;
    private IStmt selected;

    public TextMenu(InputStream input, PrintStream output) {
        this(input, output, Examples.ALL);
    }

    public TextMenu(InputStream input, PrintStream output, List<IStmt> programs) {
        this.in = new Scanner(input);
        this.out = output;
        this.programs = programs;
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = readLine();
            if (choice == null) return;          // end of input
            switch (choice.trim()) {
                case "1" -> selectProgram();
                case "2" -> runSelected();
                case "0" -> { return; }
                default -> out.println("Invalid option: " + choice.trim());
            }
        }
    }

    private void printMenu() {
        out.println();
        out.println("=== Toy interpreter ===");
        out.println("1. Select a program");
        out.println("2. Run the selected program");
        out.println("0. Exit");
        out.print("> ");
        out.flush();
    }

    private void selectProgram() {
        for (int i = 0; i < programs.size(); i++)
            out.println((i + 1) + ". " + programs.get(i));
        out.print("Program number: ");
        out.flush();
        String line = readLine();
        if (line == null) return;
        try {
            int index = Integer.parseInt(line.trim());
            if (index < 1 || index > programs.size()) {
                out.println("There is no program number " + index);
                return;
            }
            selected = programs.get(index - 1);
            out.println("Selected: " + selected);
        } catch (NumberFormatException e) {
            out.println("Please type a number, not: " + line.trim());
        }
    }

    private void runSelected() {
        if (selected == null) {
            out.println("No program selected yet. Choose option 1 first.");
            return;
        }
        // a fresh state on every run, so a program can be run more than once
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(),
                new MyList<>(), selected);
        Controller controller = new Controller(new Repository(state), true, out);
        try {
            controller.allStep();
        } catch (MyException e) {
            out.println("Error: " + e.getMessage());
        }
    }

    private String readLine() {
        return in.hasNextLine() ? in.nextLine() : null;
    }
}