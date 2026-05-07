package com.example.isib.findeds.web.api;

import com.example.isib.findeds.model.ErrorRate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FindedsApiController {

    @PostMapping("/findeds/api/calculate")
    public Map<String, Object> calculateEdsAjax(@RequestParam("amperage") double amperage,
                                                @RequestParam("inResistance") double inResistance,
                                                @RequestParam("outResistance") double outResistance,
                                                @RequestParam(value = "withErrors", defaultValue = "false") boolean withErrors,
                                                HttpSession session) {
        double trueEds = amperage * (inResistance + outResistance);
        double trueVoltageOnLoad = amperage * outResistance;

        ErrorRate errorRate = (ErrorRate) session.getAttribute("errorRate");
        if (errorRate == null) {
            errorRate = new ErrorRate();
            session.setAttribute("errorRate", errorRate);
        }

        Map<String, Double> lastParams = (Map<String, Double>) session.getAttribute("lastParams");
        Map<String, Double> currentParams = new HashMap<>();
        currentParams.put("amperage", amperage);
        currentParams.put("inResistance", inResistance);
        currentParams.put("outResistance", outResistance);

        if (lastParams != null && !lastParams.equals(currentParams)) {
            errorRate.resetMeasurements();
        }
        session.setAttribute("lastParams", currentParams);

        double displayedEds;
        double displayedVoltage;
        double absoluteError = 0.0;

        if (withErrors) {
            errorRate.setActive(true);
            displayedEds = errorRate.applyNoise(trueEds);
            displayedVoltage = errorRate.applyNoise(trueVoltageOnLoad);
            errorRate.getMeasurements().add(displayedEds);
            absoluteError = errorRate.calculateAbsoluteError();
        } else {
            errorRate.setActive(false);
            displayedEds = trueEds;
            displayedVoltage = trueVoltageOnLoad;
            errorRate.resetMeasurements();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("eds", displayedEds);
        response.put("amperage", amperage);
        response.put("outResistance", outResistance);
        response.put("voltageOnLoad", displayedVoltage);
        response.put("absoluteError", absoluteError);
        response.put("withErrors", withErrors);
        return response;
    }
}
