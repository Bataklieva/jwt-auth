package com.auth.service;

import com.auth.configuration.RsaKeyConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RsaKeyConfig rsaKeys;

    public String generateAccessToken(UserDetails user) {

        List<String> roles = user.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .toList();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(rsaKeys.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }


    public String generateRefreshToken(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7))
                .signWith(rsaKeys.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(rsaKeys.getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getRemainingValidity(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }
}