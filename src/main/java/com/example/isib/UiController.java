package com.example.isib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UiController {

    @GetMapping("/")
    public String index() {
        return "title";
    }

    @GetMapping(value = "/api/labs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> labs() throws IOException {
        ClassPathResource classPathLabs = new ClassPathResource("static/labs.json");
        String json = new String(classPathLabs.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return ResponseEntity.ok(json);
    }
}
