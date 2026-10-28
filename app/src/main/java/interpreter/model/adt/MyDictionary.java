package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class MyDictionary<K, V> implements IDictionary<K, V> {
    // LinkedHashMap is a HashMap that remembers insertion order, so the display is stable
    private final Map<K, V> map = new LinkedHashMap<>();

    @Override public boolean isDefined(K key) { return map.containsKey(key); }

    @Override public V lookup(K key) throws ADTException {
        if (!map.containsKey(key)) throw new ADTException("key " + key + " is not in the dictionary");
        return map.get(key);
    }

    @Override public void put(K key, V value) { map.put(key, value); }

    @Override public void update(K key, V value) throws ADTException {
        if (!map.containsKey(key)) throw new ADTException("key " + key + " is not in the dictionary");
        map.put(key, value);
    }

    /** e.g. {v -> 2, a -> false}. */
    @Override public String toString() {
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        map.forEach((k, v) -> sj.add(k + " -> " + v));
        return sj.toString();
    }
}