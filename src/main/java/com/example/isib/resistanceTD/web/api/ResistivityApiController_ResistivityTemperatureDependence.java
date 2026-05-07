package com.example.isib.resistanceTD.web.api;

import com.example.isib.resistanceTD.model.ResistivityForm_ResistivityTemperatureDependence;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/resistance/api")
public class ResistivityApiController_ResistivityTemperatureDependence {

    private static final Map<String, double[]> METALS = Map.of(
            "Медь", new double[]{0.0175, 0.0043},
            "Алюминий", new double[]{0.0280, 0.0040},
            "Железо", new double[]{0.1000, 0.0060},
            "Вольфрам", new double[]{0.0550, 0.0045},
            "Нихром", new double[]{1.1000, 0.0004},
            "Серебро", new double[]{0.0160, 0.0038}
    );

    @PostMapping("/calculate")
    public ResponseEntity<?> calculate(@RequestBody ResistivityForm_ResistivityTemperatureDependence form) {
        try {
            if (form.getT1() == null || form.getT2() == null ||
                    form.getLength() == null || form.getDiameterMm() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Заполните все поля"));
            }
            if (form.getDiameterMm() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Диаметр должен быть больше 0"));
            }
            if (form.getLength() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Длина должна быть больше 0"));
            }

            double[] data = METALS.get(form.getMetal());
            if (data == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Неизвестный металл"));
            }

            form.setRho1(data[0]);
            form.setAlpha(data[1]);

            double dt = form.getT2() - form.getT1();
            double k = 1 + form.getAlpha() * dt;
            double area = Math.PI * Math.pow(form.getDiameterMm() / 2, 2);
            double rho2 = form.getRho1() * k;
            double r1 = (form.getRho1() * form.getLength()) / area;
            double r2 = (rho2 * form.getLength()) / area;

            form.setDt(dt);
            form.setK(k);
            form.setAreaMm2(area);
            form.setRho2(rho2);
            form.setDeltaRho(rho2 - form.getRho1());
            form.setPercent((form.getDeltaRho() / form.getRho1()) * 100);
            form.setR1(r1);
            form.setR2(r2);

            return ResponseEntity.ok(form);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Ошибка сервера: " + e.getMessage()));
        }
    }
}
