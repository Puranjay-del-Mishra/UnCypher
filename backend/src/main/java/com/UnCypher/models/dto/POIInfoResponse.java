package com.UnCypher.models.dto;

import lombok.Data;
import java.util.List;

@Data
public class POIInfoResponse {

    private List<MapCommand> commands;

}
