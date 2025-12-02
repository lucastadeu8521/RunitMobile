package com.example.ruint;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ruint.api.ApiClient;
import com.example.ruint.api.ApiService;
import com.example.ruint.api.SessionManager;
import com.example.ruint.api.dto.LoginRequest;
import com.example.ruint.api.dto.LoginResponse;
import com.example.ruint.api.dto.RegisterRequest;
import com.example.ruint.api.dto.UserResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ApiService apiService;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getService(this);

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

            RegisterRequest request = new RegisterRequest(
                    name,
                    name,
                    "1990-01-01",
                    "OTHER",
                    "UTC",
                    "pt-BR",
                    email,
                    password
            );

            executeRegister(request, email, password);
        });

        tvLogin.setOnClickListener(v -> finish());
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void executeRegister(RegisterRequest request, String email, String password) {
        btnRegister.setEnabled(false);

        apiService.register(request).enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                btnRegister.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Cadastro realizado", Toast.LENGTH_SHORT).show();
                    performLoginAfterRegister(email, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Falha de comunicação com servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLoginAfterRegister(String email, String password) {
        apiService.login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.saveAuthSession(response.body());
                    navigateToDashboard();
                } else {
                    Toast.makeText(RegisterActivity.this, "Não foi possível entrar após cadastro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Erro ao autenticar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
