package com.example.isib.findeds.web.ui;

import com.example.isib.findeds.model.ErrorRate;
import com.example.isib.findeds.model.FindedsModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/findeds")
public class FindedsController {

    private static final double FIXED_IN_RESISTANCE = 400;
    private static final double FIXED_OUT_RESISTANCE = 1100;
    private static final double FIXED_VOLTAGE = 12;

    @GetMapping
    public String showSimulationPage(Model model, HttpSession session) {
        FindedsModel physicsModel = new FindedsModel();
        physicsModel.setVoltage(FIXED_VOLTAGE);
        physicsModel.setAmperage(6);
        physicsModel.setInResistance(FIXED_IN_RESISTANCE);
        physicsModel.setOutResistance(FIXED_OUT_RESISTANCE);
        physicsModel.calculateEds();

        model.addAttribute("physicsModel", physicsModel);
        model.addAttribute("edsValue", physicsModel.getEds());

        if (session.getAttribute("errorRate") == null) {
            session.setAttribute("errorRate", new ErrorRate());
        }

        return "findeds/findeds";
    }
}
