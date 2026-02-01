package com.wordtrainer.controller;

import com.wordtrainer.dto.request.ChildLoginRequest;
import com.wordtrainer.dto.request.LoginRequest;
import com.wordtrainer.dto.request.RegisterRequest;
import com.wordtrainer.dto.response.ApiResponse;
import com.wordtrainer.dto.response.AuthResponse;
import com.wordtrainer.repository.ChildRepository;
import com.wordtrainer.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final ChildRepository childRepository;

    public AuthController(AuthService authService, ChildRepository childRepository) {
        this.authService = authService;
        this.childRepository = childRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Inscription réussie"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Connexion réussie"));
    }

    @PostMapping("/login/child")
    public ResponseEntity<ApiResponse<AuthResponse>> loginChild(@Valid @RequestBody ChildLoginRequest request) {
        AuthResponse response = authService.loginChild(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Connexion réussie"));
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkUsername(@PathVariable String username) {
        boolean available = !childRepository.existsByUsername(username.toLowerCase());
        return ResponseEntity.ok(ApiResponse.success(Map.of("available", available)));
    }
}
