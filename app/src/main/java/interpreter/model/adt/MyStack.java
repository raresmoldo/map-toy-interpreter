package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;

public class MyStack<T> implements IStack<T> {
    private final Deque<T> stack = new ArrayDeque<>();

    @Override public void push(T v) { stack.push(v); }

    @Override public T pop() throws ADTException {
        if (stack.isEmpty()) throw new ADTException("pop from an empty stack");
        return stack.pop();
    }

    @Override public boolean isEmpty() { return stack.isEmpty(); }

    @Override public List<T> toList() {
        return Collections.unmodifiableList(new ArrayList<>(stack));
    }

    @Override public String toString() {
        StringJoiner sj = new StringJoiner(" | ", "{", "}");
        for (T t : stack) sj.add(String.valueOf(t));
        return sj.toString();
    }
}