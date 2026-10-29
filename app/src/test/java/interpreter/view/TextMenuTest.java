package interpreter.view;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class TextMenuTest {
    private static String runMenu(String input) {
        var in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        var buffer = new ByteArrayOutputStream();
        new TextMenu(in, new PrintStream(buffer, true, StandardCharsets.UTF_8)).run();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void selectedProgramRunsAndPrintsItsResult() {
        String output = runMenu("1\n2\n2\n0\n");   // select Ex2, run it, exit
        assertTrue(output.contains("Out: {18}"));
    }

    @Test
    void runningWithoutSelectionOnlyShowsAMessage() {
        String output = runMenu("2\n0\n");
        assertTrue(output.contains("No program selected"));
        assertFalse(output.contains("ExeStack"));
    }

    @Test
    void aProgramCanBeRunTwice() {
        String output = runMenu("1\n1\n2\n2\n0\n");
        assertEquals(2, output.split("Out: \\{2\\}", -1).length - 1);
    }

    @Test
    void invalidInputDoesNotCrashTheMenu() {
        String output = runMenu("hello\n1\nabc\n1\n99\n0\n");
        assertTrue(output.contains("Invalid option: hello"));
        assertTrue(output.contains("Please type a number"));
        assertTrue(output.contains("There is no program number 99"));
    }

    @Test
    void endOfInputEndsTheMenu() {
        assertDoesNotThrow(() -> runMenu(""));
    }
}