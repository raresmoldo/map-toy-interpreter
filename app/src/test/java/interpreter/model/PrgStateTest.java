package interpreter.model;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.MyException;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class PrgStateTest {
    @Test
    void theProgramIsOnTheExeStackAtTheStart() throws MyException {
        IStmt program = new NopStmt();
        PrgState state = PrgState.of(program);
        assertSame(program, state.getExeStack().pop());
    }

    @Test
    void symTableOutAndFileTableAreEmptyAtTheStart() {
        PrgState state = PrgState.of(new NopStmt());
        assertEquals("{}", state.getSymTable().toString());
        assertEquals(0, state.getOut().size());
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void theOriginalProgramIsKept() throws MyException {
        IStmt program = new NopStmt();
        PrgState state = PrgState.of(program);
        state.getExeStack().pop();
        assertSame(program, state.getOriginalProgram());
    }

    @Test
    void eachStateHasItsOwnStructures() {
        PrgState s1 = PrgState.of(new NopStmt());
        PrgState s2 = PrgState.of(new NopStmt());
        s1.getSymTable().put("v", new IntValue(1));
        assertFalse(s2.getSymTable().isDefined("v"));
    }

    @Test
    void toStringShowsTheFourStructures() {
        PrgState state = PrgState.of(new NopStmt());
        state.getSymTable().put("v", new IntValue(2));
        state.getOut().add(new IntValue(5));
        assertEquals("ExeStack: {nop}\nSymTable: {v -> 2}\nOut: {5}\nFileTable: {}",
                state.toString());
    }

    @Test
    void toStringShowsTheNamesOfTheOpenFiles() {
        PrgState state = PrgState.of(new NopStmt());
        state.getFileTable().put(new StringValue("test.in"), new BufferedReader(new StringReader("")));
        assertTrue(state.toString().endsWith("FileTable: {\"test.in\"}"));
    }

    @Test
    void toLogStringHasTheFourSectionsOneElementPerLine() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        state.getSymTable().put("v", new IntValue(2));
        state.getSymTable().put("w", new IntValue(3));
        state.getOut().add(new IntValue(5));
        state.getFileTable().put(new StringValue("test.in"), new BufferedReader(new StringReader("")));
        assertEquals("ExeStack:\nnop\nSymTable:\nv --> 2\nw --> 3\nOut:\n5\nFileTable:\ntest.in\n",
                state.toLogString());
    }

    @Test
    void toLogStringListsTheStackTopFirst() {
        PrgState state = TestUtil.emptyState();
        state.getExeStack().push(new NopStmt());
        state.getExeStack().push(new interpreter.model.stmt.CompStmt(new NopStmt(), new NopStmt()));
        assertEquals("ExeStack:\n(nop; nop)\nnop\nSymTable:\nOut:\nFileTable:\n",
                state.toLogString());
    }
}