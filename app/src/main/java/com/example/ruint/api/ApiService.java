package com.example.ruint.api;

import com.example.ruint.api.dto.LoginRequest;
import com.example.ruint.api.dto.LoginResponse;
import com.example.ruint.api.dto.RaceResponse;
import com.example.ruint.api.dto.RegisterRequest;
import com.example.ruint.api.dto.RunningSessionRequest;
import com.example.ruint.api.dto.RunningSessionResponse;
import com.example.ruint.api.dto.UpdatePasswordRequest;
import com.example.ruint.api.dto.UpdateProfileRequest;
import com.example.ruint.api.dto.UserResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<UserResponseDto> register(@Body RegisterRequest request);

    @GET("api/races")
    Call<List<RaceResponse>> getRaces();

    @GET("api/sessions")
    Call<List<RunningSessionResponse>> getSessions();

    @POST("api/sessions")
    Call<RunningSessionResponse> createSession(@Body RunningSessionRequest request);

    @PUT("api/users/profile")
    Call<UserResponseDto> updateProfile(@Body UpdateProfileRequest request);

    @PUT("api/users/password")
    Call<Void> updatePassword(@Body UpdatePasswordRequest request);
}
