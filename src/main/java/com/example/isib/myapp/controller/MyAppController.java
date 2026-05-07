package com.example.isib.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myapp")
public class MyAppController {

    @GetMapping
    public String index() {
        return "myapp/index";
    }
}
