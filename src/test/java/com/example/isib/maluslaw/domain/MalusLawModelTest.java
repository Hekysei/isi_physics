package com.example.isib.maluslaw.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MalusLawModelTest {

    @Test
    void calculate_phi0_returnsHalfIntensity() {
        MalusLawModel model = MalusLawModel.builder().intensity0(100).phi(0).build();

        double result = model.calculate();

        assertEquals(50.0, result, 0.0001);
    }

    @Test
    void calculate_phi90_returnsZero() {
        MalusLawModel model = MalusLawModel.builder().intensity0(100).phi(90).build();

        double result = model.calculate();

        assertEquals(0.0, result, 0.0001);
    }

    @Test
    void calculate_phi45_returnsQuarterIntensity() {
        MalusLawModel model = MalusLawModel.builder().intensity0(100).phi(45).build();

        double result = model.calculate();

        assertEquals(25.0, result, 0.0001);
    }

    @Test
    void calculate_updatesResultField() {
        MalusLawModel model = MalusLawModel.builder().intensity0(80).phi(60).build();

        double returned = model.calculate();

        assertEquals(returned, model.getResult(), 0.0001);
    }

    @Test
    void calculate_zeroIntensity_returnsZero() {
        MalusLawModel model = MalusLawModel.builder().intensity0(0).phi(45).build();

        double result = model.calculate();

        assertEquals(0.0, result, 0.0001);
    }

    @Test
    void calculate_negativeAngle_returnsCorrectValue() {
        MalusLawModel modelPos = MalusLawModel.builder().intensity0(100).phi(30).build();
        MalusLawModel modelNeg = MalusLawModel.builder().intensity0(100).phi(-30).build();

        assertEquals(modelPos.calculate(), modelNeg.calculate(), 0.0001);
    }

    @Test
    void calculate_phi180_returnsHalfIntensity() {
        MalusLawModel model = MalusLawModel.builder().intensity0(100).phi(180).build();

        double result = model.calculate();

        assertEquals(50.0, result, 0.0001);
    }

    @Test
    void calculate_phi360_sameAsPhi0() {
        MalusLawModel model = MalusLawModel.builder().intensity0(100).phi(360).build();

        double result = model.calculate();

        assertEquals(50.0, result, 0.0001);
    }

    @Test
    void builder_setsFieldsCorrectly() {
        MalusLawModel model = MalusLawModel.builder().intensity0(42.5).phi(30).build();

        assertEquals(42.5, model.getIntensity0(), 0.0001);
        assertEquals(30, model.getPhi(), 0.0001);
    }

    @Test
    void setter_updatesFields() {
        MalusLawModel model = MalusLawModel.builder().intensity0(0).phi(0).build();
        model.setIntensity0(200);
        model.setPhi(60);

        assertEquals(200, model.getIntensity0(), 0.0001);
        assertEquals(60, model.getPhi(), 0.0001);
    }
}
