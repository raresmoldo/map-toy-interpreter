package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() {
        state = TestUtil.emptyState();
    }

    @Test
    void pushesSecondThenFirstSoFirstIsOnTop() throws MyException {
        IStmt first = new NopStmt();
        IStmt second = new CompStmt(new NopStmt(), new NopStmt());
        new CompStmt(first, second).execute(state);
        assertSame(first, state.getExeStack().pop());
        assertSame(second, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void doesNotTouchSymTableOrOut() throws MyException {
        new CompStmt(new NopStmt(), new NopStmt()).execute(state);
        assertEquals("{}", state.getSymTable().toString());
        assertEquals(0, state.getOut().size());
    }

    @Test
    void toStringShowsBothStatements() {
        assertEquals("(nop; nop)", new CompStmt(new NopStmt(), new NopStmt()).toString());
    }
}