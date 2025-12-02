package com.example.ruint.api.dto;

public class LoginResponse {
    private String token;
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String role;

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
