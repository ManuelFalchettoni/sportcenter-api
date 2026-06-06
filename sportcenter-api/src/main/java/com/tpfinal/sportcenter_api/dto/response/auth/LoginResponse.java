package com.tpfinal.sportcenter_api.dto.response.auth;

public class LoginResponse {

    private String token;
    // Útil para que el frontend sepa cómo armar el header Authorization sin asumir.
    private String tokenType = "Bearer";

    public LoginResponse() {}

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
