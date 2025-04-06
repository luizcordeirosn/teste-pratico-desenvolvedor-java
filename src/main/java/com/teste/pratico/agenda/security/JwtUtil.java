package com.teste.pratico.agenda.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtUtil {

    private static final long EXPIRATION_TIME = 86400000;

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    public String generateToken(String username, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nome", name);

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        SecretKey key = jwtSecretKey();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @SuppressWarnings("deprecation")
    public Claims validateToken(String token) {
        SecretKey key = jwtSecretKey();
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailWithoutValidation(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
    
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    
            return claims.getSubject();
        }
    
        return "";
    }
}
