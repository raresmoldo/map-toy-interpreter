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

class ArithExpTest {
    private IDictionary<String, IValue> symTable;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    private static IExp num(int n) { return new ValueExp(new IntValue(n)); }
    private static IExp bool(boolean b) { return new ValueExp(new BoolValue(b)); }

    @Test
    void addAddsTheOperands() throws MyException {
        assertEquals(new IntValue(5), new ArithExp(ArithOp.ADD, num(2), num(3)).eval(symTable));
    }

    @Test
    void subSubtractsTheSecondFromTheFirst() throws MyException {
        assertEquals(new IntValue(-1), new ArithExp(ArithOp.SUB, num(2), num(3)).eval(symTable));
    }

    @Test
    void mulMultipliesTheOperands() throws MyException {
        assertEquals(new IntValue(6), new ArithExp(ArithOp.MUL, num(2), num(3)).eval(symTable));
    }

    @Test
    void divDividesTheFirstByTheSecond() throws MyException {
        assertEquals(new IntValue(3), new ArithExp(ArithOp.DIV, num(7), num(2)).eval(symTable));
    }

    @Test
    void respectsOperatorPrecedenceEncodedInTheTree() throws MyException {
        IExp e = new ArithExp(ArithOp.ADD, num(2),
                new ArithExp(ArithOp.MUL, num(3), num(5)));
        assertEquals(new IntValue(17), e.eval(symTable));
    }

    @Test
    void evaluatesVariablesFromTheSymbolTable() throws MyException {
        symTable.put("a", new IntValue(4));
        IExp e = new ArithExp(ArithOp.ADD, new VarExp("a"), num(1));
        assertEquals(new IntValue(5), e.eval(symTable));
    }

    @Test
    void divisionByZeroThrows() {
        IExp e = new ArithExp(ArithOp.DIV, num(1), num(0));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("division by zero", ex.getMessage());
    }

    @Test
    void firstOperandNotAnIntegerThrows() {
        IExp e = new ArithExp(ArithOp.ADD, bool(true), num(1));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("first operand is not an integer", ex.getMessage());
    }

    @Test
    void secondOperandNotAnIntegerThrows() {
        IExp e = new ArithExp(ArithOp.ADD, num(1), bool(true));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("second operand is not an integer", ex.getMessage());
    }

    @Test
    void undefinedVariableInAnOperandThrows() {
        IExp e = new ArithExp(ArithOp.ADD, new VarExp("x"), num(1));
        assertThrows(ExpressionException.class, () -> e.eval(symTable));
    }

    @Test
    void evaluationDoesNotChangeTheSymbolTable() throws MyException {
        symTable.put("a", new IntValue(4));
        new ArithExp(ArithOp.MUL, new VarExp("a"), num(2)).eval(symTable);
        assertEquals(new IntValue(4), symTable.lookup("a"));
    }

    @Test
    void toStringShowsTheParenthesisedExpression() {
        IExp e = new ArithExp(ArithOp.ADD, num(2), new ArithExp(ArithOp.MUL, num(3), num(5)));
        assertEquals("(2 + (3 * 5))", e.toString());
    }
}