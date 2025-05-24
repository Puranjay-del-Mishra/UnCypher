package com.UnCypher.models.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapCommand {
    private String type;
    private String id;
    private List<Double> coords;
    private String popupText;
    private String color;

    private Map<String, Object> geojson;
}
