package com.example.isib.findeds.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ErrorRate {
    private boolean active = false;
    private final List<Double> measurements = new ArrayList<>();
    private double studentCoefficient = 2.0;
    private double systematicError = 0.5;
    private final Random random = new Random();

    public ErrorRate() {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Double> getMeasurements() {
        return measurements;
    }

    public double getStudentCoefficient() {
        return studentCoefficient;
    }

    public void setStudentCoefficient(double studentCoefficient) {
        this.studentCoefficient = studentCoefficient;
    }

    public double getSystematicError() {
        return systematicError;
    }

    public void setSystematicError(double systematicError) {
        this.systematicError = systematicError;
    }

    public double applyNoise(double trueValue) {
        if (!active) {
            return trueValue;
        }
        double randomComponent = -1.0 + (2.0 * random.nextDouble());
        double measuredValue = trueValue + systematicError + randomComponent;
        return Math.round(measuredValue * 100.0) / 100.0;
    }

    public double calculateAbsoluteError() {
        if (measurements.isEmpty()) {
            return systematicError;
        }
        double sum = 0;
        for (double value : measurements) {
            sum += value;
        }
        double average = sum / measurements.size();

        double varianceSum = 0;
        for (double value : measurements) {
            varianceSum += Math.pow(value - average, 2);
        }

        double standardDeviation = Math.sqrt(varianceSum / Math.max(1, measurements.size() - 1));
        double standardError = standardDeviation / Math.sqrt(measurements.size());
        double statisticalError = studentCoefficient * standardError;
        return Math.round(Math.sqrt(Math.pow(systematicError, 2) + Math.pow(statisticalError, 2)) * 100.0) / 100.0;
    }

    public void resetMeasurements() {
        measurements.clear();
    }
}
