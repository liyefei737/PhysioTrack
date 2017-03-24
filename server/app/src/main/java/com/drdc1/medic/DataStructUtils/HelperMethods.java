package com.drdc1.medic.DataStructUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    public static List<Float> arrayToFloatList(float[] data) {
        if (data == null) {
            return Collections.emptyList();
        }
        List<Float> result = new ArrayList<>(data.length);
        for (float item : data) {
            result.add(item);
        }
        return result;
    }

    public static float[] toFloatArray(List<Float> floatList) {
        if (floatList == null) {
            return new float[0];
        }
        float[] result = new float[floatList.size()];
        for (int i = 0; i < result.length; i++) {
            Float boxed = floatList.get(i);
            result[i] = boxed == null ? 0.f : boxed;
        }
        return result;
    }

}
