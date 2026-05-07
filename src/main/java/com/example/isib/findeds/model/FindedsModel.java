package com.example.isib.findeds.model;

public class FindedsModel {
    private double eds;
    private double amperage;
    private double voltage;
    private double inResistance;
    private double outResistance;

    public FindedsModel() {
    }

    public double getEds() {
        return eds;
    }

    public void setEds(double eds) {
        this.eds = eds;
    }

    public double getAmperage() {
        return amperage;
    }

    public void setAmperage(double amperage) {
        this.amperage = amperage;
        calculateEds();
    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getInResistance() {
        return inResistance;
    }

    public void setInResistance(double inResistance) {
        this.inResistance = inResistance;
        calculateEds();
    }

    public double getOutResistance() {
        return outResistance;
    }

    public void setOutResistance(double outResistance) {
        this.outResistance = outResistance;
        calculateEds();
    }

    public void calculateEds() {
        this.eds = amperage * (inResistance + outResistance);
    }
}
