package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

class NopStmtTest {
    @Test
    void changesNothing() throws MyException {
        PrgState state = TestUtil.emptyState();
        state.getSymTable().put("v", new IntValue(1));
        state.getOut().add(new IntValue(2));

        PrgState result = new NopStmt().execute(state);

        assertSame(state, result);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals("{v -> 1}", state.getSymTable().toString());
        assertEquals("{2}", state.getOut().toString());
    }

    @Test
    void toStringIsNop() {
        assertEquals("nop", new NopStmt().toString());
    }
}