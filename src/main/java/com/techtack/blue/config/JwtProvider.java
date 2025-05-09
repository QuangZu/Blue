package com.techtack.blue.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Base64;

@Service
public class JwtProvider {
    // Use the recommended key generation method
    private final SecretKey key;
    
    public JwtProvider() {
        // Decode the hex string to bytes
        byte[] keyBytes = Base64.getDecoder().decode(JwtConstant.SECRET_KEY.getBytes());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication auth){
        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("email", auth.getName())
                .signWith(key)
                .compact();

        return jwt;
    }

    public String getEmailFromToken(String jwt){
        jwt=jwt.substring(7);

        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

        String email = String.valueOf(claims.get("email"));

        return email;
    }
}
