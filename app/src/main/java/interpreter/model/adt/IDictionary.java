package interpreter.model.adt;

import interpreter.exceptions.ADTException;

public interface IDictionary<K, V> {
    boolean isDefined(K key);
    V lookup(K key) throws ADTException;
    /** Adds the key or overwrites its value if the key already exists. */
    void put(K key, V value);
    /** Changes the value of an existing key, throws if the key is missing. */
    void update(K key, V value) throws ADTException;
}