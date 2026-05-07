package com.example.isib.maluslaw.web;

import com.example.isib.maluslaw.domain.MalusLawModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/maluslaw")
@Slf4j
public class MalusLawPageController {

    @GetMapping
    public String malusLaw(ModelMap map) {
        log.info("GET /maluslaw - opening Malus law laboratory page");
        MalusLawModel model = MalusLawModel.builder().intensity0(100).phi(0).build();
        model.calculate();
        map.addAttribute("model", model);
        return "maluslaw/maluslaw";
    }
}
