package com.UnCypher.utils;

import java.util.Map;

public class StateComparator {

    public static boolean hasStateChanged(Map<String, Object> current, Map<String, Object> previous, Map<String, Object> schema) {
        if (previous == null || previous.isEmpty()) return true;

        for (String key : schema.keySet()) {
            Object currVal = current.get(key);
            Object prevVal = previous.get(key);
            Object thresholdSpec = schema.get(key);

            if (thresholdSpec instanceof Map) {
                Map<String, Object> subSchema = (Map<String, Object>) thresholdSpec;
                Map<String, Object> currSub = (Map<String, Object>) currVal;
                Map<String, Object> prevSub = (Map<String, Object>) prevVal;

                for (String subKey : subSchema.keySet()) {
                    double curr = parseDouble(currSub.get(subKey));
                    double prev = parseDouble(prevSub.get(subKey));
                    double threshold = parseDouble(subSchema.get(subKey));

                    if (Math.abs(curr - prev) > threshold) {
                        return true;
                    }
                }
            } else {
                double curr = parseDouble(currVal);
                double prev = parseDouble(prevVal);
                double threshold = parseDouble(thresholdSpec);

                if (Math.abs(curr - prev) > threshold) {
                    return true;
                }
            }
        }
        return false;
    }

    private static double parseDouble(Object value) {
        if (value == null) return 0.0;
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
