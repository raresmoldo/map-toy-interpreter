package interpreter.model.value;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import org.junit.jupiter.api.Test;

class ValueTest {
    @Test
    void intValueHasIntType() {
        assertEquals(new IntType(), new IntValue(5).getType());
    }

    @Test
    void boolValueHasBoolType() {
        assertEquals(new BoolType(), new BoolValue(true).getType());
    }

    @Test
    void valuesWithSameContentAreEqual() {
        assertEquals(new IntValue(3), new IntValue(3));
        assertEquals(new BoolValue(false), new BoolValue(false));
    }

    @Test
    void valuesOfDifferentTypesAreNotEqual() {
        assertNotEquals(new IntValue(1), new BoolValue(true));
    }

    @Test
    void toStringShowsTheRawValue() {
        assertEquals("42", new IntValue(42).toString());
        assertEquals("true", new BoolValue(true).toString());
    }
}