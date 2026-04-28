package com.isib.physics.features.maluslaw.api;

import com.isib.physics.features.maluslaw.domain.ErrorRate;
import com.isib.physics.features.maluslaw.domain.MalusLawModel;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final ErrorRate errorRate;

    public ApiController(ObjectProvider<ErrorRate> errorRateProvider) {
        this.errorRate = errorRateProvider.getIfAvailable(ErrorRate::new);
    }

    @GetMapping("/api/calculate")
    public Map<String, Double> calculateApi(
        @RequestParam double intensity0,
        @RequestParam double phi
    ) {
        MalusLawModel model = MalusLawModel.builder().intensity0(intensity0).phi(phi).build();
        double trueResult = model.calculate();
        double measuredResult = errorRate.applyNoise(trueResult);
        if (errorRate.isActive()) {
            errorRate.getMeasurements().add(measuredResult);
        }
        return Map.of("result", measuredResult);
    }

    @GetMapping("/api/error")
    public Map<String, Object> getError() {
        return Map.of(
            "absoluteError", errorRate.calculateAbsoluteError(),
            "measurementsCount", errorRate.getMeasurements().size(),
            "noiseActive", errorRate.isActive()
        );
    }

    @PostMapping("/api/error/toggle")
    public Map<String, Boolean> toggleNoise() {
        errorRate.setActive(!errorRate.isActive());
        return Map.of("noiseActive", errorRate.isActive());
    }

    @PostMapping("/api/error/reset")
    public Map<String, String> resetMeasurements() {
        errorRate.resetMeasurements();
        return Map.of("status", "ok");
    }
}

