package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyDictionaryTest {
    private IDictionary<String, Integer> dict;

    @BeforeEach
    void setUp() {
        dict = new MyDictionary<>();
    }

    @Test
    void putThenLookupReturnsTheValue() throws ADTException {
        dict.put("a", 1);
        assertEquals(1, dict.lookup("a"));
    }

    @Test
    void isDefinedIsTrueOnlyForExistingKeys() {
        dict.put("a", 1);
        assertTrue(dict.isDefined("a"));
        assertFalse(dict.isDefined("b"));
    }

    @Test
    void updateChangesTheValueOfAnExistingKey() throws ADTException {
        dict.put("a", 1);
        dict.update("a", 5);
        assertEquals(5, dict.lookup("a"));
    }

    @Test
    void putOverwritesAnExistingKey() throws ADTException {
        dict.put("a", 1);
        dict.put("a", 9);
        assertEquals(9, dict.lookup("a"));
    }

    @Test
    void lookupOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.lookup("missing"));
    }

    @Test
    void updateOfMissingKeyThrows() {
        assertThrows(ADTException.class, () -> dict.update("missing", 1));
    }

    @Test
    void updateOfMissingKeyDoesNotAddIt() {
        assertThrows(ADTException.class, () -> dict.update("missing", 1));
        assertFalse(dict.isDefined("missing"));
    }

    @Test
    void toStringShowsAllPairs() {
        dict.put("v", 2);
        dict.put("a", 7);
        assertEquals("{v -> 2, a -> 7}", dict.toString());
    }
}