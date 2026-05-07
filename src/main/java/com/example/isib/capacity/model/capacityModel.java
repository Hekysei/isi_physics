package com.example.isib.capacity.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class capacityModel {
    public static final double EPSILON_0 = 8.85e-12;

    private double epsilon = 1.0;
    private double square;
    private double distance = 1.0;
    private double radius1 = 1.0;
    private double radius2 = 2.0;
    private double length = 1.0;

    private double capacity1;
    private double capacity2;
    private double capacity3;

    private final capacityErrorRate capacityErrorRate = new capacityErrorRate();

    public void calculateTheoreticalCapacities() {
        if (distance != 0) {
            this.capacity1 = (epsilon * EPSILON_0 * square) / distance;
        }

        if (radius1 > 0 && radius2 > radius1) {
            this.capacity2 = (2 * Math.PI * epsilon * EPSILON_0 * length) / Math.log(radius2 / radius1);
        }

        if (radius2 > radius1) {
            this.capacity3 = (4 * Math.PI * epsilon * EPSILON_0 * radius1 * radius2) / (radius2 - radius1);
        }
    }

    public double getMeasuredCapacity1() {
        return capacityErrorRate.applyNoise(this.capacity1);
    }

    public double getMeasuredCapacity2() {
        return capacityErrorRate.applyNoise(this.capacity2);
    }

    public double getMeasuredCapacity3() {
        return capacityErrorRate.applyNoise(this.capacity3);
    }
}
