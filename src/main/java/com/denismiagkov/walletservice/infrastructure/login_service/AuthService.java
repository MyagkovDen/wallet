package com.denismiagkov.walletservice.infrastructure.login_service;

import com.denismiagkov.walletservice.application.service.Service;
import com.denismiagkov.walletservice.domain.model.Entry;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;


public class AuthService {

    private final Service service;
    private final JwtProvider jwtProvider;

    public AuthService() {
        this.service = new Service();
        this.jwtProvider = new JwtProvider();
    }

    public JwtResponse login(JwtRequest authRequest) {
        Entry entry = service.getEntryByLogin(authRequest.getLogin());
        if (entry.getPassword().equals(authRequest.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(entry);
            String refreshToken = jwtProvider.generateRefreshToken(entry);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Неправильный пароль");
        }
    }

    public JwtResponse getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            Entry entry = service.getEntryByLogin(login);
            String accessToken = jwtProvider.generateAccessToken(entry);
            return new JwtResponse(accessToken, null);
        }
        return new JwtResponse(null, null);
    }

    public String validateAccessToken(String httpRequestHeader) throws RuntimeException{
        String token = getTokenFromHeader(httpRequestHeader);
        String login = getLoginFromToken(token);
        jwtProvider.validateAccessToken(token);
        return login;
    }

    public boolean validateRefreshToken(String refreshToken) {
        return jwtProvider.validateRefreshToken(refreshToken);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public String getTokenFromHeader(String header) {
        return header.substring(7);
    }

    public String getLoginFromToken(String token) {
        return jwtProvider.getLoginFromToken(token);
    }

}

