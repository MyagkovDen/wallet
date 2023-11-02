package com.denismiagkov.walletservice.infrastructure.login_service;

import org.springframework.stereotype.Component;

@Component
public class JwtResponse {
    public String accessToken;
    public String refreshToken;

    public JwtResponse(String accessToken, String refreshToken) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
