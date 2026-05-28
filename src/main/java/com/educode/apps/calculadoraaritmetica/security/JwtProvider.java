package com.educode.apps.calculadoraaritmetica.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecurityProperties securityProperties;

    public JwtProvider(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public String generateToken(Authentication username) {
        return Jwts.builder()
                .setSubject(username.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.securityProperties.getExpiration()))
                .signWith(SignatureAlgorithm.HS512, this.securityProperties.getSecretKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(this.securityProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(this.securityProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
