package interpreter.model.type;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

class TypeTest {
    @Test
    void defaultValueOfIntIsZero() {
        assertEquals(new IntValue(0), new IntType().defaultValue());
    }

    @Test
    void defaultValueOfBoolIsFalse() {
        assertEquals(new BoolValue(false), new BoolType().defaultValue());
    }

    @Test
    void intAndBoolTypesAreDifferent() {
        assertNotEquals(new IntType(), new BoolType());
    }

    @Test
    void sameTypesAreEqual() {
        assertEquals(new IntType(), new IntType());
    }

    @Test
    void toStringUsesLanguageKeywords() {
        assertEquals("int", new IntType().toString());
        assertEquals("bool", new BoolType().toString());
    }
}