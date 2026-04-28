package com.isib.physics.features.maluslaw.web;

import com.isib.physics.features.maluslaw.domain.MalusLawModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class MalusLawPageController {

    @GetMapping("/malus-law")
    public String malusLaw(ModelMap map) {
        log.info("GET /malus-law - opening Malus law laboratory page");
        MalusLawModel model = MalusLawModel.builder().intensity0(100).phi(0).build();
        model.calculate();
        map.addAttribute("model", model);
        return "malus-law";
    }
}

