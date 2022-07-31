package ir.sooall.poker.cordiantor.db;

import java.util.concurrent.ConcurrentHashMap;

public class Table<T, V> {
    private final ConcurrentHashMap<T, V> map;

    public Table(ConcurrentHashMap<T, V> map) {
        this.map = map;
    }

    public V findById(T id) {
        return map.get(id);
    }

    public V save(T t, V v) {
        return map.put(t, v);
    }
}