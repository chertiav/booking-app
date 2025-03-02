package com.chertiavdev.bookingapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secret;
    @Value("${jwt.expiration}")
    private long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secretString) {
        secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        log.info("Generating JWT token for user: {}", username);
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secret)
                .compact();
        log.info("JWT token generated successfully for user: {}", username);
        return token;
    }

    public boolean isValidToken(String token) {
        try {
            log.debug("Validating JWT token");
            boolean isValid = !parseClaims(token).getExpiration().before(new Date());
            if (isValid) {
                log.info("JWT token is valid");
            } else {
                log.warn("JWT token is expired");
            }
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Expired or invalid JWT token");
        }
    }

    public String getUsername(String token) {
        log.debug("Extracting username from JWT token");
        String username = getClaimFromToken(token, Claims::getSubject);
        log.info("Username extracted from JWT token: {}", username);
        return username;

    }

    private Claims parseClaims(String token) {
        log.debug("Parsing claims from JWT token");
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        log.debug("Extracting claim from token");
        final Claims claims = parseClaims(token);
        return claimsResolver.apply(claims);
    }
}
