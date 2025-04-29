package com.UnCypher.services.impl;

import com.UnCypher.services.LocalityResolver;
import org.springframework.stereotype.Service;

@Service
public class GeoHashLocalityResolver implements LocalityResolver {

    @Override
    public String resolveLocalityId(double lat, double lng) {
        // TODO: Replace with polygon matcher or AWS Location Service
        return "geo-" + Math.round(lat * 1000) + "-" + Math.round(lng * 1000);
    }
}
