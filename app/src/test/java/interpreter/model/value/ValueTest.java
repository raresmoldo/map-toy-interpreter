package interpreter.model.value;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
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
    @Test
    void stringValueHasStringType() {
        assertEquals(new StringType(), new StringValue("a").getType());
    }

    @Test
    void equalStringValuesAreEqualAndHaveTheSameHashCode() {
        assertEquals(new StringValue("test.in"), new StringValue("test.in"));
        assertEquals(new StringValue("test.in").hashCode(), new StringValue("test.in").hashCode());
    }

    @Test
    void differentStringValuesAreNotEqual() {
        assertNotEquals(new StringValue("a"), new StringValue("b"));
    }

    @Test
    void stringValueToStringHasQuotes() {
        assertEquals("\"abc\"", new StringValue("abc").toString());
    }

    @Test
    void stringValuesWorkAsKeysOfADictionary() throws ADTException {
        IDictionary<StringValue, Integer> dict = new MyDictionary<>();
        dict.put(new StringValue("f"), 1);
        assertEquals(1, dict.lookup(new StringValue("f")));
    }
}