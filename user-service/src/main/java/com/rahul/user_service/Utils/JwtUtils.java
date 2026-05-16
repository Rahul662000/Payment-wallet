package com.rahul.user_service.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String GenerateAccessToken(String email){
        return Jwts.builder()
                .subject(email)
                .claim("type","access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getSecretKey())
                .compact();
    }

    public String GenerateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim("type","refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(getSecretKey())
                .compact();
    }

    public boolean validateToken(String token , UserDetails userDetails){
        String userName = extractUserName(token);

        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserName(String token){
        return extractClaims(token).getSubject();
    }

    public boolean isAccessToken(String token) {
        Claims claims = extractClaims(token);
        return "access".equals(
                claims.get("type")
        );
    }

    public boolean isRefreshToken(String refreshToken) {
        Claims claims = extractClaims(refreshToken);
        return "refresh".equals(
                claims.get("type")
        );
    }
}



