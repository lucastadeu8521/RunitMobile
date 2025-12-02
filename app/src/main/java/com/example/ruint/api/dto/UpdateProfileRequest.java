package com.example.ruint.api.dto;

public class UpdateProfileRequest {
    private String name;
    private String lastName;
    private String birthDate;
    private String gender;
    private String timezone;
    private String locale;
    private String profilePictureUrl;

    public UpdateProfileRequest(String name, String lastName, String birthDate, String gender, String timezone,
                                String locale, String profilePictureUrl) {
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.timezone = timezone;
        this.locale = locale;
        this.profilePictureUrl = profilePictureUrl;
    }
}
