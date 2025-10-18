package com.epam.finaltask.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    public static final String SECRET_KEY = "hokbQeTILXB7Z4KHBhooIxbkOzBzrVn9";
    private static final long ACCESS_TOKEN_EXP_MS = 3600000; // 1 година
    private static final long REFRESH_TOKEN_EXP_MS = 7 * 24 * 60 * 60 * 1000; // 7 днів

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // --- Access Token ---
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Refresh Token ---
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public long getAccessTokenExpirationMs() {
        return ACCESS_TOKEN_EXP_MS;
    }

    public long getRefreshTokenExpirationMs() {
        return REFRESH_TOKEN_EXP_MS;
    }
}
