package com.github.mlwilli.authservice.api.dto;

import java.util.Set;

public class UserResponse {

    private Long id;
    private String username;
    private boolean enabled;
    private Set<String> roles;

    public UserResponse() {
    }

    public Long getId() {
        return id;
    }

    public UserResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public UserResponse setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public UserResponse setRoles(Set<String> roles) {
        this.roles = roles;
        return this;
    }
}
