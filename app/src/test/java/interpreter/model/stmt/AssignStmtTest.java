package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() throws MyException {
        state = TestUtil.emptyState();
        new VarDeclStmt("v", new IntType()).execute(state);
        new VarDeclStmt("b", new BoolType()).execute(state);
    }

    @Test
    void assignsTheValueOfTheExpression() throws MyException {
        new AssignStmt("v", new ValueExp(new IntValue(2))).execute(state);
        assertEquals(new IntValue(2), state.getSymTable().lookup("v"));
    }

    @Test
    void assignsABooleanToABoolVariable() throws MyException {
        new AssignStmt("b", new ValueExp(new BoolValue(true))).execute(state);
        assertEquals(new BoolValue(true), state.getSymTable().lookup("b"));
    }

    @Test
    void assignmentToAnUndeclaredVariableThrows() {
        var ex = assertThrows(StatementException.class,
                () -> new AssignStmt("x", new ValueExp(new IntValue(1))).execute(state));
        assertEquals("variable x was not declared", ex.getMessage());
    }

    @Test
    void assigningABooleanToAnIntVariableThrows() {
        assertThrows(StatementException.class,
                () -> new AssignStmt("v", new ValueExp(new BoolValue(true))).execute(state));
    }

    @Test
    void assigningAnIntegerToABoolVariableThrows() {
        assertThrows(StatementException.class,
                () -> new AssignStmt("b", new ValueExp(new IntValue(1))).execute(state));
    }

    @Test
    void failedAssignmentLeavesTheOldValue() throws MyException {
        new AssignStmt("v", new ValueExp(new IntValue(5))).execute(state);
        assertThrows(StatementException.class,
                () -> new AssignStmt("v", new ValueExp(new BoolValue(true))).execute(state));
        assertEquals(new IntValue(5), state.getSymTable().lookup("v"));
    }

    @Test
    void errorInTheExpressionIsPropagated() {
        assertThrows(ExpressionException.class,
                () -> new AssignStmt("v", new VarExp("undefined")).execute(state));
    }

    @Test
    void toStringShowsTheAssignment() {
        assertEquals("v = 2", new AssignStmt("v", new ValueExp(new IntValue(2))).toString());
    }
}