package com.example.user.isotuapp.Model;

public class MarkerData {
    private String id;
    private String imageUrl;
    private double lat;
    private double lng;
    private String name;

    public MarkerData() {
    }

    public MarkerData(String id, String imageUrl, double lat, double lng, String name) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }
}
