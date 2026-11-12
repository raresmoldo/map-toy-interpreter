package interpreter.view;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RunExampleCommandTest {
    @TempDir
    Path dir;

    private ByteArrayOutputStream buffer;
    private PrintStream out;

    @BeforeEach
    void setUp() {
        buffer = new ByteArrayOutputStream();
        out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
    }

    private PrgState state;

    private RunExampleCommand commandFor(IStmt program) {
        state = TestUtil.stateFor(program);
        var repo = new Repository(state, dir.resolve("log.txt").toString());
        return new RunExampleCommand("1", "example", new Controller(repo, false), out);
    }

    private String output() { return buffer.toString(StandardCharsets.UTF_8); }

    @Test
    void executingTheCommandRunsTheProgram() throws MyException {
        commandFor(Examples.EX1).execute();
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void anExampleRunsOnlyOnce() throws MyException {
        RunExampleCommand command = commandFor(Examples.EX1);
        command.execute();
        command.execute();
        assertTrue(output().contains("This example has already been executed."));
        assertFalse(output().contains("Error"));
        assertEquals(1, state.getOut().size());
    }

    @Test
    void anErrorInTheProgramIsShownAndDoesNotEscape() {
        RunExampleCommand command = commandFor(new PrintStmt(new VarExp("undefined")));
        assertDoesNotThrow(command::execute);
        assertTrue(output().contains("Error: variable undefined is not defined"));
    }

    @Test
    void theCommandKeepsItsKeyAndDescription() {
        RunExampleCommand command = commandFor(Examples.EX1);
        assertEquals("1", command.getKey());
        assertEquals("example", command.getDescription());
    }
}