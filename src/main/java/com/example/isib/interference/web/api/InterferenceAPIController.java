package com.example.isib.interference.web.api;

import com.example.isib.interference.model.InterferenceModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interference")
public class InterferenceAPIController {

    private final InterferenceModel interferenceModel = new InterferenceModel();

    @PostMapping
    public Map<String, Object> calculate(
            @RequestParam double wavelength,
            @RequestParam double distance,
            @RequestParam double slitWidth,
            @RequestParam int slitNumber,
            @RequestParam double sourceAngle) {

        Map<String, Object> response = new HashMap<>();

        try {
            response.put("wavelength", wavelength);
            response.put("distance", distance);
            response.put("slitWidth", slitWidth);
            response.put("slitNumber", slitNumber);
            response.put("sourceAngle", sourceAngle);

            InterferenceModel.CalculationResult result = interferenceModel.calculateAll(
                    wavelength, slitWidth, sourceAngle, slitNumber);

            response.put("resultIntensity", String.format("%.2f", result.getIntensity()));
            response.put("resultType", result.getType());
            response.put("pathDifference", String.format("%.3f", result.getPathDifference()));
            response.put("sinTheta", String.format("%.4f", result.getSinTheta()));
            response.put("phaseDiff", String.format("%.2f", result.getPhaseDiff()));
            response.put("order", String.format("%.3f", result.getOrder()));
            response.put("maxWavelength", String.format("%.1f", result.getMaxWavelength()));
            response.put("maxOrder", result.getMaxOrder());
            response.put("minWavelength", String.format("%.1f", result.getMinWavelength()));
            response.put("minOrder", result.getMinOrder());
            response.put("nearbyExtremums", result.getNearbyExtremums());
        } catch (Exception e) {
            response.put("error", "Ошибка: " + e.getMessage());
        }

        return response;
    }
}
