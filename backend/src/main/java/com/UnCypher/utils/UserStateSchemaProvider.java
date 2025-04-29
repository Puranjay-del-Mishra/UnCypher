package com.UnCypher.utils;

import java.util.HashMap;
import java.util.Map;

public class UserStateSchemaProvider {

    public static Map<String, Object> getSchemaForUser(String userId, String deviceType) {
        Map<String, Object> schema = new HashMap<>();

        Map<String, Object> location = new HashMap<>();
        location.put("lat", 0.0005);
        location.put("lng", 0.0005);

        schema.put("location", location);

        if ("wearable".equalsIgnoreCase(deviceType)) {
            schema.put("heartRate", 5);
            schema.put("stressLevel", 1.5);
        }

        if ("mobile".equalsIgnoreCase(deviceType)) {
            schema.put("speed", 1.0);
        }

        return schema;
    }
}
