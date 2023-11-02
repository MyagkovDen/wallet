package com.denismiagkov.walletservice.infrastructure.login_service;

import com.denismiagkov.walletservice.init.PropertyFile;
import com.denismiagkov.walletservice.application.service.Service;
import com.denismiagkov.walletservice.domain.model.Entry;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey JWT_ACCESS_SECRET_KEY;
    private final SecretKey JWT_REFRESH_SECRET_KEY;
    private Service service;

    @Autowired
    public JwtProvider() {
        String valueOfJwtAccessSecretKey = PropertyFile.getProperties("JWT_ACCESS_SECRET_KEY");
        String valueOfJwtRefreshSecretKey = PropertyFile.getProperties("JWT_REFRESH_SECRET_KEY");
        this.JWT_ACCESS_SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(valueOfJwtAccessSecretKey));
        this.JWT_REFRESH_SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(valueOfJwtRefreshSecretKey));
        this.service = new Service();
    }

    public String generateAccessToken(Entry entry) {
        LocalDateTime now = LocalDateTime.now();
        Instant accessExpirationInstant = now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(entry.getLogin())
                .setExpiration(accessExpiration)
                .claim("userId", service.getPlayerByLogin(entry.getLogin()).getId())
                .signWith(JWT_ACCESS_SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(Entry entry) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(entry.getLogin())
                .setExpiration(refreshExpiration)
                .signWith(JWT_REFRESH_SECRET_KEY)
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, JWT_ACCESS_SECRET_KEY);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, JWT_REFRESH_SECRET_KEY);
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, JWT_ACCESS_SECRET_KEY);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, JWT_REFRESH_SECRET_KEY);
    }

    private Claims getClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_ACCESS_SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        String login = claims.getSubject();
        return login;
    }
}
