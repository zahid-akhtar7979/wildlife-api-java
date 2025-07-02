package com.wildlife.shared.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Welcome controller providing basic API information and health endpoints.
 */
@RestController
public class WelcomeController {

    @Value("${spring.application.name:wildlife-api}")
    private String serviceName;

    @Value("${spring.application.version:1.0.0}")
    private String version;

    @Value("${wildlife.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    /**
     * Root endpoint providing API information
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", serviceName);
        response.put("version", version);
        response.put("status", "running");
        response.put("description", "Wildlife Conservation Platform REST API");
        response.put("timestamp", LocalDateTime.now());
        
        // API endpoints
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("articles", "/api/articles");
        endpoints.put("users", "/api/users");
        endpoints.put("health", "/actuator/health");
        endpoints.put("api_docs", "/swagger-ui.html");
        response.put("endpoints", endpoints);
        
        // Authentication info
        Map<String, String> auth = new HashMap<>();
        auth.put("type", "JWT Bearer Token");
        auth.put("login_endpoint", "/api/auth/login");
        auth.put("token_header", "Authorization: Bearer <token>");
        response.put("authentication", auth);
        
        response.put("frontend_origins", allowedOrigins);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Favicon endpoint to prevent 404 errors
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }
} 