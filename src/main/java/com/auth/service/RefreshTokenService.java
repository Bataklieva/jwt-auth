package com.auth.service;

import com.auth.entity.RefreshToken;
import com.auth.entity.User;
import com.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshToken createRefreshToken(User user, String token) {

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        return repository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked())
            throw new RuntimeException("Refresh token revoked");

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Refresh token expired");

        return refreshToken;
    }

    public void revokeToken(String token) {
        repository.findByToken(token)
                .ifPresent(t -> {
                    t.setRevoked(true);
                    repository.save(t);
                });
    }
}
