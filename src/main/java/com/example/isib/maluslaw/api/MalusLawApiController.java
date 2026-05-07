package com.example.isib.maluslaw.api;

import com.example.isib.maluslaw.domain.MalusLawModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maluslaw/api")
public class MalusLawApiController {

    @GetMapping("/calculate")
    public MalusLawResponse calculate(@RequestParam double intensity0, @RequestParam double phi) {
        MalusLawModel model = MalusLawModel.builder().intensity0(intensity0).phi(phi).build();
        model.calculate();
        return new MalusLawResponse(model.getResult());
    }

    public record MalusLawResponse(double result) {
    }
}
