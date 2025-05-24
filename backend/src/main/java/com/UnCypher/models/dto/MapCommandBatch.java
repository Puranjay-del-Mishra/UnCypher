package com.UnCypher.models.dto;

import lombok.Data;
import java.util.List;

@Data
public class MapCommandBatch {

    private String userId;
    private List<MapCommand> commands;

}
