package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.TestUtil;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VarDeclStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() {
        state = TestUtil.emptyState();
    }

    @Test
    void declaringAnIntInitialisesItToZero() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("v"));
    }

    @Test
    void declaringABoolInitialisesItToFalse() throws MyException {
        new VarDeclStmt("b", new BoolType()).execute(state);
        assertEquals(new BoolValue(false), state.getSymTable().lookup("b"));
    }

    @Test
    void declaringTheSameVariableTwiceThrows() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        var ex = assertThrows(StatementException.class,
                () -> new VarDeclStmt("v", new IntType()).execute(state));
        assertEquals("variable v is already declared", ex.getMessage());
    }

    @Test
    void redeclarationWithAnotherTypeAlsoThrows() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        assertThrows(StatementException.class,
                () -> new VarDeclStmt("v", new BoolType()).execute(state));
    }

    @Test
    void failedRedeclarationKeepsTheOldValue() throws MyException {
        new VarDeclStmt("v", new IntType()).execute(state);
        state.getSymTable().update("v", new IntValue(9));
        assertThrows(StatementException.class,
                () -> new VarDeclStmt("v", new IntType()).execute(state));
        assertEquals(new IntValue(9), state.getSymTable().lookup("v"));
    }

    @Test
    void toStringShowsTypeAndName() {
        assertEquals("int v", new VarDeclStmt("v", new IntType()).toString());
    }
}