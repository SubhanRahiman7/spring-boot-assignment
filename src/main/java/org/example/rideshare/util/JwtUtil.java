package org.example.rideshare.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String ROLE_CLAIM_KEY = "role";

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private Long tokenExpirationMillis;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    public String generateToken(String username, String role) {
        Map<String, Object> tokenClaims = buildTokenClaims(role);
        return buildJwtToken(tokenClaims, username);
    }

    private Map<String, Object> buildTokenClaims(String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLE_CLAIM_KEY, role);
        return claims;
    }

    private String buildJwtToken(Map<String, Object> claims, String subject) {
        long currentTime = System.currentTimeMillis();
        Date issuedAt = new Date(currentTime);
        Date expirationTime = new Date(currentTime + tokenExpirationMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationTime)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get(ROLE_CLAIM_KEY, String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims allClaims = parseTokenClaims(token);
        return claimsResolver.apply(allClaims);
    }

    private Claims parseTokenClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean checkTokenExpiration(String token) {
        Date expirationDate = extractExpiration(token);
        Date currentDate = Date.from(Instant.now());
        return expirationDate.before(currentDate);
    }

    public Boolean validateToken(String token, String username) {
        String tokenUsername = extractUsername(token);
        boolean isUsernameValid = tokenUsername.equals(username);
        boolean isNotExpired = !checkTokenExpiration(token);
        return isUsernameValid && isNotExpired;
    }
}

