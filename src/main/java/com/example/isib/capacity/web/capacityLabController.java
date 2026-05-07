package com.example.isib.capacity.web;

import com.example.isib.capacity.model.capacityModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/capacity")
public class capacityLabController {
    @GetMapping
    public String index() {
        return "capacity/capacityindex";
    }

    @PostMapping("/calculate")
    public String calculate(@ModelAttribute("labData") capacityModel labData, org.springframework.ui.Model model) {
        labData.calculateTheoreticalCapacities();
        model.addAttribute("labData", labData);
        return "capacity/capacityindex";
    }
}
