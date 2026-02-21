package com.auth.service;

import com.auth.dto.AuthRequest;
import com.auth.dto.AuthResponse;
import com.auth.dto.RefreshTokenRequest;
import com.auth.entity.RefreshToken;
import com.auth.entity.User;
import com.auth.entity.enums.Role;
import com.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(AuthRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role(Role.ROLE_USER).build();
        repo.save(user);

        return generateTokens(user);
    }

    public AuthResponse login(AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = repo.findByUsername(request.getUsername()).get();
        return generateTokens(user);
    }

    private AuthResponse generateTokens(User user) {
        UserDetails userDetails = buildUserDetails(user);
        String access = jwtService.generateAccessToken(userDetails);
        String refresh = jwtService.generateRefreshToken(userDetails);
        refreshTokenService.createRefreshToken(user, refresh);
        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    private UserDetails buildUserDetails (User user) {
        return new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                );
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken storedToken =
                refreshTokenService.validateRefreshToken(request.getRefreshToken());
        User user = storedToken.getUser();
        return generateTokens(user);
    }

}
