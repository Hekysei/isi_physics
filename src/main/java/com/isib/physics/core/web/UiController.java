package com.isib.physics.core.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UiController {

    @GetMapping("/")
    public String index(ModelMap map) {
        map.addAttribute("model", new Object());
        return "title";
    }

    @GetMapping(value = "/api/labs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> labs() throws IOException {
        return ResponseEntity.ok(readLabsJson());
    }

    private String readLabsJson() throws IOException {
        ClassPathResource classPathLabs = new ClassPathResource("static/labs.json");
        return new String(classPathLabs.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}

