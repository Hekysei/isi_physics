package com.example.isib.capacity.config;

import com.example.isib.capacity.service.capacityJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/capacity/api/auth")
public class capacityAuthController {

    @Autowired
    private capacityJwtService capacityJwtService;

    @PostMapping("/token")
    public Map<String, String> getToken(@RequestBody(required = false) Map<String, String> request) {
        String username = request != null ? request.getOrDefault("username", "anonymous") : "anonymous";
        String role = request != null ? request.getOrDefault("role", "GUEST") : "GUEST";

        String token = capacityJwtService.generateToken(username, role);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        response.put("role", role);
        return response;
    }

    @GetMapping("/test-token")
    public Map<String, String> getTestToken() {
        return getToken(Map.of("username", "test_user", "role", "TESTER"));
    }
}
