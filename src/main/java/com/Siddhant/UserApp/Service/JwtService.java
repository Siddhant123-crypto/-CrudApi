package com.Siddhant.UserApp.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretString;
    private SecretKey key;
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }private static final long EXPIRATION_TIME =
            7L * 24 * 60 * 60 * 1000;
    public String generateToken(
            String username,
            String role) {
        Map<String, Object> claims =
                new HashMap<>();
        claims.put("role", role);
        Date now = new Date();
        Date expiration =
                new Date(now.getTime()+ EXPIRATION_TIME);
        return Jwts.builder().claims(claims).subject(username).issuedAt(now).expiration(expiration).signWith(key).compact();
    }public String extractUsername(
            String token) {
        return extractClaim(token,Claims::getSubject );
    }public String extractRole(
            String token) {
        return extractClaim(
        token,
                claims ->
                        claims.get(
                                "role",
                                String.class
                        )
        );
    }public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {
        Claims claims =
                extractAllClaims(token);
        return claimsResolver.apply(claims);
    }private Claims extractAllClaims(
            String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }public boolean validateToken(
            String token,
            String username) {
        String extractedUsername =
                extractUsername(token);
        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }private boolean isTokenExpired(
            String token) {
        return extractClaim(
                token,
                Claims::getExpiration
        ).before(new Date());
    }
}