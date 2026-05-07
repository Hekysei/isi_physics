package com.example.isib.resistanceTD.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResistivityForm_ResistivityTemperatureDependence {

    private String metal;
    private Double t1;
    private Double t2;
    private Double alpha;
    private Double rho1;
    private Double length;
    private Double diameterMm;
    private Double r1;
    private Double rho2;
    private Double deltaRho;
    private Double percent;
    private Double dt;
    private Double k;
    private Double areaMm2;
    private Double r2;
    private String error;

    public boolean isValidBase() {
        return t1 != null && t2 != null && alpha != null;
    }
}
