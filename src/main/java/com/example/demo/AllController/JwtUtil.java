package com.example.demo.AllController;



import java.util.Date;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "MySuperSecretKeykiujhgjhfgjhfuyfjhfvjhjukugyuigtyuifgjhgjfytj";

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
