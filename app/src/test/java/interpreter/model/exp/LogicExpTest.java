package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogicExpTest {
    private IDictionary<String, IValue> symTable;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    private static IExp bool(boolean b) { return new ValueExp(new BoolValue(b)); }
    private static IExp num(int n) { return new ValueExp(new IntValue(n)); }

    @Test
    void andTruthTable() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.AND, bool(true), bool(true)).eval(symTable));
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.AND, bool(true), bool(false)).eval(symTable));
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.AND, bool(false), bool(true)).eval(symTable));
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.AND, bool(false), bool(false)).eval(symTable));
    }

    @Test
    void orTruthTable() throws MyException {
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.OR, bool(true), bool(true)).eval(symTable));
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.OR, bool(true), bool(false)).eval(symTable));
        assertEquals(new BoolValue(true), new LogicExp(LogicOp.OR, bool(false), bool(true)).eval(symTable));
        assertEquals(new BoolValue(false), new LogicExp(LogicOp.OR, bool(false), bool(false)).eval(symTable));
    }

    @Test
    void firstOperandNotABooleanThrows() {
        IExp e = new LogicExp(LogicOp.AND, num(1), bool(true));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("first operand is not a boolean", ex.getMessage());
    }

    @Test
    void secondOperandNotABooleanThrows() {
        IExp e = new LogicExp(LogicOp.OR, bool(true), num(1));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("second operand is not a boolean", ex.getMessage());
    }

    @Test
    void evaluatesVariablesFromTheSymbolTable() throws MyException {
        symTable.put("a", new BoolValue(true));
        IExp e = new LogicExp(LogicOp.AND, new VarExp("a"), bool(true));
        assertEquals(new BoolValue(true), e.eval(symTable));
    }

    @Test
    void toStringShowsTheOperator() {
        assertEquals("(true and false)", new LogicExp(LogicOp.AND, bool(true), bool(false)).toString());
        assertEquals("(true or false)", new LogicExp(LogicOp.OR, bool(true), bool(false)).toString());
    }
}