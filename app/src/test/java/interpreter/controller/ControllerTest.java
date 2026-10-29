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
import java.util.List;
import org.junit.jupiter.api.Test;

class ControllerTest {
    private static PrgState run(IStmt program) throws MyException {
        PrgState state = TestUtil.stateFor(program);
        new Controller(new Repository(state), false).allStep(); // display off
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
    void oneStepOnAnEmptyStackThrows() {
        PrgState state = TestUtil.emptyState();
        Controller controller = new Controller(new Repository(state), false);
        var ex = assertThrows(StatementException.class, () -> controller.oneStep(state));
        assertEquals("execution stack is empty", ex.getMessage());
    }

    @Test
    void oneStepExecutesExactlyOneStatement() throws MyException {
        PrgState state = TestUtil.stateFor(new CompStmt(new NopStmt(), new NopStmt()));
        Controller controller = new Controller(new Repository(state), false);
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
        Controller controller = new Controller(new Repository(state), false);
        controller.allStep();
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }

    private static List<String> displayedLines(IStmt program) throws MyException {
        PrgState state = TestUtil.stateFor(program);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        var out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        new Controller(new Repository(state), true, out).allStep();
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
        new Controller(new Repository(state), false, out).allStep();
        assertEquals("", buffer.toString(StandardCharsets.UTF_8));
    }
}