package interpreter.model;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.MyException;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

class PrgStateTest {
    @Test
    void theProgramIsOnTheExeStackAtTheStart() throws MyException {
        IStmt program = new NopStmt();
        PrgState state = TestUtil.stateFor(program);
        assertSame(program, state.getExeStack().pop());
    }

    @Test
    void symTableAndOutAreEmptyAtTheStart() {
        PrgState state = TestUtil.stateFor(new NopStmt());
        assertEquals("{}", state.getSymTable().toString());
        assertEquals(0, state.getOut().size());
    }

    @Test
    void theOriginalProgramIsKept() throws MyException {
        IStmt program = new NopStmt();
        PrgState state = TestUtil.stateFor(program);
        state.getExeStack().pop();
        assertSame(program, state.getOriginalProgram());
    }

    @Test
    void toStringShowsTheThreeStructures() {
        PrgState state = TestUtil.stateFor(new NopStmt());
        state.getSymTable().put("v", new IntValue(2));
        state.getOut().add(new IntValue(5));
        assertEquals("ExeStack: {nop}\nSymTable: {v -> 2}\nOut: {5}", state.toString());
    }
}