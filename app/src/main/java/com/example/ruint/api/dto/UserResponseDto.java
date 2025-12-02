package com.example.ruint.api.dto;

public class UserResponseDto {
    private Long id;
    private String name;
    private String lastName;
    private String birthDate;
    private String gender;
    private String timezone;
    private String locale;
    private String email;
    private String role;
    private String profilePictureUrl;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getBirthDate() { return birthDate; }
    public String getGender() { return gender; }
    public String getTimezone() { return timezone; }
    public String getLocale() { return locale; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
}
