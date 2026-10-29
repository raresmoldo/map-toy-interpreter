package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

class ValueExpTest {
    private final IDictionary<String, IValue> symTable = new MyDictionary<>();

    @Test
    void returnsTheConstantItWraps() {
        assertEquals(new IntValue(7), new ValueExp(new IntValue(7)).eval(symTable));
        assertEquals(new BoolValue(true), new ValueExp(new BoolValue(true)).eval(symTable));
    }

    @Test
    void toStringShowsTheValue() {
        assertEquals("7", new ValueExp(new IntValue(7)).toString());
    }
}