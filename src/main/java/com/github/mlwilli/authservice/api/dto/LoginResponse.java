package com.github.mlwilli.authservice.api.dto;

import java.util.List;

public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private String username;
    private List<String> roles;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public String getTokenType() {
        return tokenType;
    }

    public LoginResponse setTokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public LoginResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public List<String> getRoles() {
        return roles;
    }

    public LoginResponse setRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }
}
