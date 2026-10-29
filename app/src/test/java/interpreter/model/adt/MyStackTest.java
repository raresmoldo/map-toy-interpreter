package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyStackTest {
    private IStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new MyStack<>();
    }

    @Test
    void popReturnsElementsInLifoOrder() throws ADTException {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void popOnEmptyStackThrows() {
        var ex = assertThrows(ADTException.class, () -> stack.pop());
        assertEquals("pop from an empty stack", ex.getMessage());
    }

    @Test
    void popOnStackEmptiedByPopsThrows() throws ADTException {
        stack.push(1);
        stack.pop();
        assertThrows(ADTException.class, () -> stack.pop());
    }

    @Test
    void isEmptyReflectsContent() throws ADTException {
        assertTrue(stack.isEmpty());
        stack.push(1);
        assertFalse(stack.isEmpty());
        stack.pop();
        assertTrue(stack.isEmpty());
    }

    @Test
    void toStringShowsTopFirst() {
        stack.push(3);
        stack.push(2);
        stack.push(1);
        assertEquals("{1 | 2 | 3}", stack.toString());
    }

    @Test
    void toStringOfEmptyStack() {
        assertEquals("{}", stack.toString());
    }
}