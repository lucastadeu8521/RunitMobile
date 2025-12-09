package com.example.ruint;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ruint.api.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            executeRegister(name, email, password);
        });

        tvLogin.setOnClickListener(v -> finish());
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void executeRegister(String name, String email, String password) {
        btnRegister.setEnabled(false);

        if (sessionManager.hasUser() && !email.equalsIgnoreCase(sessionManager.getUserEmail())) {
            Toast.makeText(this, "Um usuário local já está cadastrado", Toast.LENGTH_SHORT).show();
            btnRegister.setEnabled(true);
            return;
        }

        sessionManager.saveLocalUser(name, email, password);
        Toast.makeText(RegisterActivity.this, "Cadastro realizado localmente", Toast.LENGTH_SHORT).show();
        navigateToDashboard();
    }
}
