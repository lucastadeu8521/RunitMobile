package com.example.ruint.api.dto;

public class RegisterRequest {
    private String name;
    private String lastName;
    private String birthDate;
    private String gender;
    private String timezone;
    private String locale;
    private String email;
    private String password;

    public RegisterRequest(String name, String lastName, String birthDate, String gender,
                           String timezone, String locale, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.timezone = timezone;
        this.locale = locale;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getLocale() {
        return locale;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
