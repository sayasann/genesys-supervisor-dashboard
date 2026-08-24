package com.genesys.jwt;

import com.genesys.jwt.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties properties;

    private final SecretKey signingKey;

    public JwtService(JwtProperties properties){
        this.properties=properties;
        this.signingKey= Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username){
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.expirationMinutes()*60);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();

    }

    public String extractUsername(String token){
        return parseClaims(token).getSubject();
    }

    public boolean isValid(String token){
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException | io.jsonwebtoken.security.SecurityException | IllegalArgumentException e) {
           return false;
        }
    }

    private Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
