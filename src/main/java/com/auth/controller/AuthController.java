package com.auth.controller;

import com.auth.dto.AuthRequest;
import com.auth.dto.AuthResponse;
import com.auth.dto.RefreshTokenRequest;
import com.auth.service.AuthService;
import com.auth.service.JwtService;
import com.auth.service.RefreshTokenService;
import com.auth.service.TokenBlacklistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    private final AuthService service;
    @Autowired
    private final RefreshTokenService refreshTokenService;
    @Autowired
    JwtService jwtService;
    @Autowired
    TokenBlacklistService blacklistService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return service.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RefreshTokenRequest request) {

        String accessToken = authHeader.substring(7);
        long remainingTime = jwtService.getRemainingValidity(accessToken);
        blacklistService.blacklistToken(accessToken, remainingTime);
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }


}

