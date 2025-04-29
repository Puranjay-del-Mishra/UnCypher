package com.UnCypher.models;

import lombok.Data;
import java.util.Map;

@Data
public class UserStateCacheEntry {
    private Map<String, Object> state;
    private String localityId;
    private String lastUpdated;
}
