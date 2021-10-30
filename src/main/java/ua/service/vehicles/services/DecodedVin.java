package ua.service.vehicles.services;

import java.io.Serializable;

public class DecodedVin implements Serializable {

    private String make;
    private String model;
    private int year;

    public DecodedVin(String make, String model, int year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }
}
