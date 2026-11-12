package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.CompStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ControllerTest {
    @TempDir
    Path dir;

    private Controller controller(PrgState state, boolean display) {
        return new Controller(new Repository(state, dir.resolve("log.txt").toString()), display);
    }

    private PrgState run(IStmt program) throws MyException {
        PrgState state = TestUtil.stateFor(program);
        controller(state, false).allStep(); // display off
        return state;
    }

    @Test
    void example1PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX1);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(2), state.getOut().get(0));
        assertEquals(new IntValue(2), state.getSymTable().lookup("v"));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.EX2);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(18), state.getOut().get(0));
        assertEquals(new IntValue(17), state.getSymTable().lookup("a"));
        assertEquals(new IntValue(18), state.getSymTable().lookup("b"));
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX3);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(2), state.getOut().get(0));
        assertEquals(new BoolValue(true), state.getSymTable().lookup("a"));
        assertEquals(new IntValue(2), state.getSymTable().lookup("v"));
    }

    @Test
    void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(2, s.getOut().size());
        assertEquals(new IntValue(15), s.getOut().get(0));
        assertEquals(new IntValue(50), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleOnAnEmptyFilePrintsZeroZero() throws Exception {
        Path in = dir.resolve("empty.in");
        Files.writeString(in, "");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(new IntValue(0), s.getOut().get(0));
        assertEquals(new IntValue(0), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleOnAMissingFileStopsWithAnException() {
        String missing = dir.resolve("missing.in").toString();
        assertThrows(StatementException.class, () -> run(Examples.fileExample(missing)));
    }

    @Test
    void example5PrintsFive() throws MyException {
        PrgState state = run(Examples.EX5);
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(5), state.getOut().get(0));
    }

    @Test
    void oneStepOnAnEmptyStackThrows() {
        PrgState state = TestUtil.emptyState();
        Controller controller = controller(state, false);
        var ex = assertThrows(StatementException.class, () -> controller.oneStep(state));
        assertEquals("execution stack is empty", ex.getMessage());
    }

    @Test
    void oneStepExecutesExactlyOneStatement() throws MyException {
        PrgState state = TestUtil.stateFor(new CompStmt(new NopStmt(), new NopStmt()));
        Controller controller = controller(state, false);
        controller.oneStep(state);              // S1: the composition is split in two
        assertEquals("{nop | nop}", state.getExeStack().toString());
        controller.oneStep(state);
        assertEquals("{nop}", state.getExeStack().toString());
    }

    @Test
    void anErrorInTheProgramStopsTheExecutionWithAnException() {
        IStmt program = new CompStmt(new PrintStmt(new VarExp("undefined")), new NopStmt());
        assertThrows(ExpressionException.class, () -> run(program));
    }

    @Test
    void aFurtherStepAfterTheEndIsAnError() throws MyException {
        PrgState state = TestUtil.stateFor(new NopStmt());
        Controller controller = controller(state, false);
        controller.allStep();
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }

    private List<String> displayedLines(IStmt program) throws MyException {
        PrgState state = TestUtil.stateFor(program);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        var out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        new Controller(new Repository(state, dir.resolve("log.txt").toString()), true, out).allStep();
        return buffer.toString(StandardCharsets.UTF_8).lines().toList();
    }

    @Test
    void displayShowsTheSixStatesOfExample1() throws MyException {
        List<String> lines = displayedLines(Examples.EX1);
        List<String> stacks = lines.stream().filter(l -> l.startsWith("ExeStack: ")).toList();
        List<String> tables = lines.stream().filter(l -> l.startsWith("SymTable: ")).toList();
        List<String> outs = lines.stream().filter(l -> l.startsWith("Out: ")).toList();

        assertEquals(List.of(
                "ExeStack: {(int v; (v = 2; print(v)))}",
                "ExeStack: {int v | (v = 2; print(v))}",
                "ExeStack: {(v = 2; print(v))}",
                "ExeStack: {v = 2 | print(v)}",
                "ExeStack: {print(v)}",
                "ExeStack: {}"), stacks);
        assertEquals(List.of(
                "SymTable: {}", "SymTable: {}", "SymTable: {v -> 0}",
                "SymTable: {v -> 0}", "SymTable: {v -> 2}", "SymTable: {v -> 2}"), tables);
        assertEquals(List.of(
                "Out: {}", "Out: {}", "Out: {}", "Out: {}", "Out: {}", "Out: {2}"), outs);
    }

    @Test
    void displayOffPrintsNothing() throws MyException {
        PrgState state = TestUtil.stateFor(Examples.EX1);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        var out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        new Controller(new Repository(state, dir.resolve("log.txt").toString()), false, out).allStep();
        assertEquals("", buffer.toString(StandardCharsets.UTF_8));
    }

    @Test
    void logFileContainsTheFourSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:");
        assertTrue(0 <= a && a < b && b < c && c < d);
    }

    @Test
    void logFileHasTheInitialStateAndOneStatePerStep() throws Exception {
        run(Examples.EX1); // 5 steps: 6 states
        String log = Files.readString(dir.resolve("log.txt"));
        assertEquals(6, log.split("ExeStack:", -1).length - 1);
        assertTrue(log.startsWith("ExeStack:\n(int v; (v = 2; print(v)))\nSymTable:\nOut:\nFileTable:\n\n"));
    }

    @Test
    void logFileShowsTheOpenFileInTheFileTable() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        run(Examples.fileExample(in.toString()));
        String log = Files.readString(dir.resolve("log.txt"));
        assertTrue(log.contains("varf --> \"" + in + "\"\n"));
        assertTrue(log.contains("FileTable:\n" + in + "\n"));
    }
}