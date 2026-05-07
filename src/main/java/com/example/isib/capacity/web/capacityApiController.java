package com.example.isib.capacity.web;

import com.example.isib.capacity.model.capacityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/capacity/api")
public class capacityApiController {

    @GetMapping("/flat")
    public double calculateFlatApi(
            @RequestParam double square,
            @RequestParam double distance) {

        capacityModel capacityModel = new capacityModel();
        capacityModel.setSquare(square);
        capacityModel.setDistance(distance);

        capacityModel.calculateTheoreticalCapacities();

        return capacityModel.getMeasuredCapacity1();
    }

    @GetMapping("/cyl")
    public double calculateCylApi(
            @RequestParam double radius1,
            @RequestParam double radius2,
            @RequestParam double length) {
        capacityModel capacityModel = new capacityModel();
        capacityModel.setRadius1(radius1);
        capacityModel.setRadius2(radius2);
        capacityModel.setLength(length);
        capacityModel.calculateTheoreticalCapacities();

        return capacityModel.getMeasuredCapacity2();
    }

    @GetMapping("/sph")
    public double calculateSphApi(
            @RequestParam double radius1,
            @RequestParam double radius2) {
        capacityModel capacityModel = new capacityModel();
        capacityModel.setRadius1(radius1);
        capacityModel.setRadius2(radius2);
        capacityModel.calculateTheoreticalCapacities();

        return capacityModel.getMeasuredCapacity3();
    }
}
