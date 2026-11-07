package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class MyList<T> implements IList<T> {
    private final List<T> list = new ArrayList<>();

    @Override public void add(T v) { list.add(v); }

    @Override public T get(int index) throws ADTException {
        if (index < 0 || index >= list.size())
            throw new ADTException("index " + index + " is out of bounds (size " + list.size() + ")");
        return list.get(index);
    }

    @Override public int size() { return list.size(); }

    @Override public List<T> toList() {
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    @Override public String toString() {
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        for (T t : list) sj.add(String.valueOf(t));
        return sj.toString();
    }
}