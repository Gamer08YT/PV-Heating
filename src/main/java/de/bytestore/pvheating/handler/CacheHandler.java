package de.bytestore.pvheating.handler;

import lombok.Getter;

import java.util.HashMap;

public class CacheHandler {
    @Getter
    private static HashMap<String, Object> cache = new HashMap<String, Object>();

    /**
     * Sets the value associated with the given key in the cache.
     *
     * @param keyIO the key with which the specified value is to be associated
     * @param valueIO the value to be associated with the specified key
     */
    public static void setValue(String keyIO, Object valueIO) {
        if(cache.containsKey(keyIO))
            cache.replace(keyIO, valueIO);
        else
            cache.put(keyIO, valueIO);
    }

    /**
     * Retrieves the value associated with the given key from the cache.
     *
     * @param keyIO the key whose associated value is to be retrieved from the cache
     * @return the value associated with the specified key, or null if the key is not present in the cache
     */
    public static Object getValue(String keyIO) {
        return cache.get(keyIO);
    }

    /**
     * Retrieves the value associated with the given key from the cache and returns it. If the key is not present in the cache, it returns the default value provided.
     *
     * @param keyIO the key whose associated value is to be retrieved from the cache
     * @param defaultValue the value to be returned if the key is not present in the cache
     * @return the value associated with the specified key if it exists in the cache, otherwise the default value
     */
    public static Object getValueOrDefault(String keyIO, Object defaultValue) {
        return (getValue(keyIO) != null ? getValue(keyIO) : defaultValue);
    }
}
