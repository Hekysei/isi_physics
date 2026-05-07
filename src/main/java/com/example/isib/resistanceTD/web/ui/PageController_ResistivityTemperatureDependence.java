package com.example.isib.resistanceTD.web.ui;

import com.example.isib.resistanceTD.model.ResistivityForm_ResistivityTemperatureDependence;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/resistance")
public class PageController_ResistivityTemperatureDependence {

    @GetMapping
    public String startPage() {
        return "resistanceTD/page_start_ResistivityTemperatureDependence";
    }

    @GetMapping("/calculator")
    public String home(Model model) {
        model.addAttribute("form", new ResistivityForm_ResistivityTemperatureDependence());
        model.addAttribute("metals", List.of(
                "Медь", "Алюминий", "Железо", "Вольфрам", "Нихром", "Серебро"
        ));
        return "resistanceTD/resistance_ResistivityTemperatureDependence";
    }
}
