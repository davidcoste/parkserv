package com.dcoste.parkserv.model;

public class CarParkResponse {

    private final CarPark carPark;
    private final Double distance ;

    public CarParkResponse(CarPark carPark, Double distance) {
        this.carPark = carPark;
        this.distance = distance;
    }

    public CarPark getCarPark() {
        return carPark;
    }

    public Double getDistance() {
        return distance;
    }
}
