package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrintStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() {
        state = TestUtil.emptyState();
    }

    @Test
    void appendsTheValueToOut() throws MyException {
        new PrintStmt(new ValueExp(new IntValue(4))).execute(state);
        assertEquals(1, state.getOut().size());
        assertEquals(new IntValue(4), state.getOut().get(0));
    }

    @Test
    void keepsTheOrderOfPrinting() throws MyException {
        new PrintStmt(new ValueExp(new IntValue(1))).execute(state);
        new PrintStmt(new ValueExp(new IntValue(2))).execute(state);
        assertEquals(new IntValue(1), state.getOut().get(0));
        assertEquals(new IntValue(2), state.getOut().get(1));
    }

    @Test
    void printsTheCurrentValueOfAVariable() throws MyException {
        state.getSymTable().put("v", new IntValue(8));
        new PrintStmt(new VarExp("v")).execute(state);
        assertEquals(new IntValue(8), state.getOut().get(0));
    }

    @Test
    void errorInTheExpressionIsPropagatedAndNothingIsPrinted() {
        assertThrows(ExpressionException.class,
                () -> new PrintStmt(new VarExp("undefined")).execute(state));
        assertEquals(0, state.getOut().size());
    }

    @Test
    void toStringShowsThePrintCall() {
        assertEquals("print(v)", new PrintStmt(new VarExp("v")).toString());
    }
}