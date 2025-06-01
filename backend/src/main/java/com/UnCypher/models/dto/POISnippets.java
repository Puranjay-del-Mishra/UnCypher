package com.UnCypher.models.dto;

public class POISnippets {
    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final int distance;
    private final String formattedAddress;
    private final String locality;
    private final String region;
    private final String country;
    private final String crossStreet;
    private final String postcode;
    private final String timezone;
    private final String primaryCategory;
    private final String categoryIcon;
    private final String chainName;
    private final String status;

    public POISnippets(String id, String name, double latitude, double longitude, int distance,
                      String formattedAddress, String locality, String region, String country,
                      String crossStreet, String postcode, String timezone,
                      String primaryCategory, String categoryIcon, String chainName, String status) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.formattedAddress = formattedAddress;
        this.locality = locality;
        this.region = region;
        this.country = country;
        this.crossStreet = crossStreet;
        this.postcode = postcode;
        this.timezone = timezone;
        this.primaryCategory = primaryCategory;
        this.categoryIcon = categoryIcon;
        this.chainName = chainName;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getDistance() { return distance; }
    public String getFormattedAddress() { return formattedAddress; }
    public String getLocality() { return locality; }
    public String getRegion() { return region; }
    public String getCountry() { return country; }
    public String getCrossStreet() { return crossStreet; }
    public String getPostcode() { return postcode; }
    public String getTimezone() { return timezone; }
    public String getPrimaryCategory() { return primaryCategory; }
    public String getCategoryIcon() { return categoryIcon; }
    public String getChainName() { return chainName; }
    public String getStatus() { return status; }
}
