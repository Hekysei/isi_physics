package com.isib.physics.features.kirchhoff.model;

import java.util.Random;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KirchhoffErrorRate {
    private boolean active = true;
    private double voltageRelativeError = 0.01;
    private double resistorRelativeError = 0.02;

    private final Random random = new Random();

    public double applyVoltageSourceError(double nominalVoltage) {
        return applyRelativeError(nominalVoltage, voltageRelativeError);
    }

    public double applyResistorNominalError(double nominalResistance) {
        return applyRelativeError(nominalResistance, resistorRelativeError);
    }

    private double applyRelativeError(double value, double relativeErrorHalfRange) {
        if (!active) {
            return value;
        }
        double delta = (-relativeErrorHalfRange) + (2.0 * relativeErrorHalfRange * random.nextDouble());
        return value * (1.0 + delta);
    }
}

