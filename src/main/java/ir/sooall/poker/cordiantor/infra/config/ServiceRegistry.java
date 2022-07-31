package ir.sooall.poker.cordiantor.infra.config;

import java.util.HashMap;

public class ServiceRegistry {

    private static final HashMap<Class<?>, Object> container = new HashMap<>();

    public static <T> void addService(Class<T> clazz, T t) {
        container.put(clazz, t);
    }


    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz) {
        return (T) container.get(clazz);
    }
}
