package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyListTest {
    private IList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new MyList<>();
    }

    @Test
    void addAppendsAtTheEnd() throws ADTException {
        list.add(10);
        list.add(20);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
    }

    @Test
    void sizeCountsTheElements() {
        assertEquals(0, list.size());
        list.add(1);
        list.add(2);
        assertEquals(2, list.size());
    }

    @Test
    void getWithIndexEqualToSizeThrows() {
        list.add(1);
        assertThrows(ADTException.class, () -> list.get(1));
    }

    @Test
    void getWithNegativeIndexThrows() {
        list.add(1);
        assertThrows(ADTException.class, () -> list.get(-1));
    }

    @Test
    void getOnEmptyListThrows() {
        assertThrows(ADTException.class, () -> list.get(0));
    }

    @Test
    void toStringShowsElementsInOrder() {
        list.add(1);
        list.add(2);
        assertEquals("{1, 2}", list.toString());
    }
}