package com.example.ruint.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.ruint.api.dto.LoginResponse;

public class SessionManager {

    private static final String PREFS_NAME = "RunTrackerPrefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveAuthSession(LoginResponse response) {
        if (response == null) return;

        sharedPreferences.edit()
                .putString(KEY_AUTH_TOKEN, response.getToken())
                .putString(KEY_USER_NAME, response.getName())
                .putString(KEY_USER_EMAIL, response.getEmail())
                .putString(KEY_USER_ID, response.getId() != null ? String.valueOf(response.getId()) : "")
                .apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, "");
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getAuthToken());
    }

    public void clearSession() {
        sharedPreferences.edit()
                .remove(KEY_AUTH_TOKEN)
                .remove(KEY_USER_NAME)
                .remove(KEY_USER_EMAIL)
                .remove(KEY_USER_ID)
                .apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }
}
