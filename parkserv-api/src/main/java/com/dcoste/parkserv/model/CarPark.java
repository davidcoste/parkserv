package com.dcoste.parkserv.model;

public class CarPark {

    private final String name ;
    private final String address ;
    private final Double latitude ;
    private final Double longitude ;
    private final Integer capacity ;
    private final Integer free ;

    public CarPark(String name, String address, Double latitude, Double longitude, Integer capacity, Integer free) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.free = free;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getFree() {
        return free;
    }
}
