package com.tpfinal.sportcenter_api.dto.response.auth;

public class LoginResponse {

    private String token;
    // Útil para que el frontend sepa cómo armar el header Authorization sin asumir.
    private String tokenType = "Bearer";
    // Vida del token en segundos (convención OAuth2): le permite al frontend
    // anticipar la expiración sin decodificar el claim exp del JWT.
    private Long expiresIn;

    public LoginResponse() {}

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
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

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
