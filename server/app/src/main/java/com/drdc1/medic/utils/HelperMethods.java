package com.drdc1.medic.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper methods
 */

public class HelperMethods {
    public static <K, V> Map<K, V> deepCopy(Map<K, V> original) {
        if (original == null) return null;
        Map<K, V> copy = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : original.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }
}
