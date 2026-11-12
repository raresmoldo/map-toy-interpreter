package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IfStmtTest {
    private PrgState state;
    private final IStmt thenS = new PrintStmt(new ValueExp(new IntValue(1)));
    private final IStmt elseS = new PrintStmt(new ValueExp(new IntValue(2)));

    @BeforeEach
    void setUp() {
        state = TestUtil.emptyState();
    }

    @Test
    void trueConditionPushesTheThenBranch() throws MyException {
        new IfStmt(new ValueExp(new BoolValue(true)), thenS, elseS).execute(state);
        assertSame(thenS, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void falseConditionPushesTheElseBranch() throws MyException {
        new IfStmt(new ValueExp(new BoolValue(false)), thenS, elseS).execute(state);
        assertSame(elseS, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void conditionIsReadFromTheSymbolTable() throws MyException {
        state.getSymTable().put("c", new BoolValue(true));
        new IfStmt(new VarExp("c"), thenS, elseS).execute(state);
        assertSame(thenS, state.getExeStack().pop());
    }

    @Test
    void nonBooleanConditionThrows() {
        var ex = assertThrows(StatementException.class,
                () -> new IfStmt(new ValueExp(new IntValue(1)), thenS, elseS).execute(state));
        assertEquals("condition of the if statement is not a boolean", ex.getMessage());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void errorInTheConditionIsPropagated() {
        assertThrows(ExpressionException.class,
                () -> new IfStmt(new VarExp("undefined"), thenS, elseS).execute(state));
    }

    @Test
    void toStringShowsBothBranches() {
        var s = new IfStmt(new ValueExp(new BoolValue(true)), new NopStmt(), new NopStmt());
        assertEquals("(if (true) then (nop) else (nop))", s.toString());    }
}