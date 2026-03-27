package com.affidock.api.modules.auth.controller;

import com.affidock.api.modules.auth.dto.GoogleExchangeRequest;
import com.affidock.api.modules.auth.dto.LoginRequest;
import com.affidock.api.modules.auth.dto.LogoutRequest;
import com.affidock.api.modules.auth.dto.RegisterRequest;
import com.affidock.api.modules.auth.dto.RefreshTokenRequest;
import com.affidock.api.modules.auth.dto.SetupPasswordRequest;
import com.affidock.api.modules.auth.dto.TokenResponse;
import com.affidock.api.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/google/exchange")
    public ResponseEntity<TokenResponse> exchangeGoogle(@Valid @RequestBody GoogleExchangeRequest request) {
        return ResponseEntity.ok(authService.exchangeGoogle(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/password/setup")
    public ResponseEntity<Void> setupPassword(@Valid @RequestBody SetupPasswordRequest request) {
        authService.setupPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.revoke(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
