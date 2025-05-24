package com.UnCypher.models.dto;

import lombok.Data;

@Data
public class POIInfoRequest {

    private String userId;
    private String locality;
    private String category;

}
