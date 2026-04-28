package com.isib.physics.features.kirchhoff.model;

public record KirchhoffSimulationOutcome(
    KirchhoffCircuitData requestedData,
    KirchhoffCircuitData effectiveData,
    KirchhoffCircuitResults results
) {}

