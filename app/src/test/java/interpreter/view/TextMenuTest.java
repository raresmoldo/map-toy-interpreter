package interpreter.view;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextMenuTest {
    private List<String> executed;
    private ByteArrayOutputStream buffer;

    /** A command that only records that it was executed. */
    private Command recording(String key, String description) {
        return new Command(key, description) {
            @Override public void execute() { executed.add(key); }
        };
    }

    @BeforeEach
    void setUp() {
        executed = new ArrayList<>();
        buffer = new ByteArrayOutputStream();
    }

    private String show(String input, Command... commands) {
        var in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        TextMenu menu = new TextMenu(in, new PrintStream(buffer, true, StandardCharsets.UTF_8));
        for (Command c : commands) menu.addCommand(c);
        menu.show();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void theMenuListsTheCommandsInTheOrderTheyWereAdded() {
        String output = show("", recording("0", "exit"), recording("1", "first"), recording("2", "second"));
        int a = output.indexOf("0 : exit"), b = output.indexOf("1 : first"), c = output.indexOf("2 : second");
        assertTrue(0 <= a && a < b && b < c);
    }

    @Test
    void theChosenCommandIsExecuted() {
        show("1\n", recording("1", "first"), recording("2", "second"));
        assertEquals(List.of("1"), executed);
    }

    @Test
    void severalCommandsAreExecutedInTheOrderChosen() {
        show("2\n1\n2\n", recording("1", "first"), recording("2", "second"));
        assertEquals(List.of("2", "1", "2"), executed);
    }

    @Test
    void anInvalidOptionDoesNotCrashAndExecutesNothing() {
        String output = show("hello\n99\n1\n", recording("1", "first"));
        assertTrue(output.contains("Invalid option"));
        assertEquals(List.of("1"), executed);
    }

    @Test
    void spacesAroundTheOptionAreIgnored() {
        show("  1  \n", recording("1", "first"));
        assertEquals(List.of("1"), executed);
    }

    @Test
    void endOfInputEndsTheMenu() {
        assertDoesNotThrow(() -> show(""));
    }

    @Test
    void commandsExposeTheirKeyAndDescription() {
        Command c = recording("7", "seventh");
        assertEquals("7", c.getKey());
        assertEquals("seventh", c.getDescription());
    }
}