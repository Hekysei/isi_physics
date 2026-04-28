package com.isib.physics.features.maluslaw.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class ErrorRate {
    private boolean active = true;
    private final List<Double> measurements = new ArrayList<>();
    private final Double studentCoefficient = 2.0;
    private final Double systematicError = 0.5;
    private Double lastTrueValue;
    private Double lastMeasuredValue;

    private final Random random = new Random();

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Double> getMeasurements() {
        return measurements;
    }

    public double applyNoise(double trueValue) {
        if (!active || trueValue < 0) {
            return trueValue;
        }

        double result;
        int retries = 0;
        do {
            double randomComponent = -1.0 + (2.0 * random.nextDouble());
            double measuredValue = trueValue + systematicError + randomComponent;
            result = Math.round(measuredValue * 100.0) / 100.0;
            retries++;
        } while (retries < 10
            && lastTrueValue != null
            && Double.compare(lastTrueValue, trueValue) == 0
            && lastMeasuredValue != null
            && Double.compare(lastMeasuredValue, result) == 0);

        lastTrueValue = trueValue;
        lastMeasuredValue = result;
        return result;
    }

    public double calculateAbsoluteError() {
        if (measurements.isEmpty()) {
            return systematicError;
        }

        double sum = 0;
        for (double m : measurements) {
            sum += m;
        }
        double average = sum / measurements.size();

        double varianceSum = 0;
        for (double m : measurements) {
            varianceSum += Math.pow(m - average, 2);
        }

        int n = measurements.size();
        double standardDeviation = Math.sqrt(varianceSum / Math.max(1, n - 1));
        double standardError = standardDeviation / Math.sqrt(n);
        double statisticalError = studentCoefficient * standardError;
        double totalError = Math.sqrt(Math.pow(systematicError, 2) + Math.pow(statisticalError, 2));

        return Math.round(totalError * 100.0) / 100.0;
    }

    public void resetMeasurements() {
        measurements.clear();
        lastTrueValue = null;
        lastMeasuredValue = null;
    }
}

