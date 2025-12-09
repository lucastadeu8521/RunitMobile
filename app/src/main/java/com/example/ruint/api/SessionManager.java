package com.example.ruint.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SessionManager {

    private static final String PREFS_NAME = "RunTrackerPrefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_PASSWORD = "user_password";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveLocalUser(String name, String email, String password) {
        sharedPreferences.edit()
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_PASSWORD, password)
                .putString(KEY_AUTH_TOKEN, generateLocalToken())
                .apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, "");
    }

    public boolean hasUser() {
        return !TextUtils.isEmpty(getUserEmail()) && !TextUtils.isEmpty(getUserPassword());
    }

    public boolean authenticate(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            return false;
        }

        boolean matches = email.equalsIgnoreCase(getUserEmail()) && password.equals(getUserPassword());
        if (matches && TextUtils.isEmpty(getAuthToken())) {
            sharedPreferences.edit().putString(KEY_AUTH_TOKEN, generateLocalToken()).apply();
        }
        return matches;
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
                .remove(KEY_USER_PASSWORD)
                .apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPassword() {
        return sharedPreferences.getString(KEY_USER_PASSWORD, "");
    }

    private String generateLocalToken() {
        return "local_token_" + System.currentTimeMillis();
    }
}
