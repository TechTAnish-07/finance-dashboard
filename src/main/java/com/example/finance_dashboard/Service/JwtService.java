package com.example.finance_dashboard.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {


    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private long expirationMs;

    private SecretKey signingKey;



    @PostConstruct
    public void init() {
        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // --------------------------
    // ACCESS TOKEN (15 min)
    // --------------------------
    public String generateToken(String email, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("displayName", username);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email) // subject = email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername()) // email
                .setExpiration(new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)))
                .signWith(signingKey)
                .compact();
    }



    public boolean isTokenValid(String token , UserDetails user) {
        String email = extractEmail(token);

        return email.equals(user.getUsername()) && !isExpired(token);
    }
    public boolean isRefreshTokenValid(String token, UserDetails user) {
        String email = extractEmail(token);
        return email.equals(user.getUsername()) && !isExpired(token);
    }

    // --------------------------
    // Extract Information
    // --------------------------
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // --------------------------
    // Parse Claims
    // --------------------------
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
