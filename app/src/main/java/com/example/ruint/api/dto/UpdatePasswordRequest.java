package com.example.ruint.api.dto;

public class UpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirmation;

    public UpdatePasswordRequest(String currentPassword, String newPassword, String newPasswordConfirmation) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
