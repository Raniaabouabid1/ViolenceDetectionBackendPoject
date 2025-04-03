package com.upf.violencedetectionbackendlogic.utils;

import com.upf.violencedetectionbackendlogic.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "your_strong_secret_key_that_is_long_enough"; // Replace with your key
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours


    // Generate token using the user's id as the subject.
    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority()); // e.g., "ROLE_ADMIN"
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getId().toString())  // user ID
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    // Extract the user id (as a string) from the token.
    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        String tokenUserId = extractUserId(token);
        // In your CustomUserDetails, getUsername() now returns the email.
        // So if you want to validate by id, you'll need to compare the id from your CustomUserDetails.
        // For example, if you modify CustomUserDetails to have a getId() method:
        if(userDetails instanceof CustomUserDetails) {
            String idFromDetails = ((CustomUserDetails) userDetails).getId().toString();
            return tokenUserId.equals(idFromDetails) && !isTokenExpired(token);
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
