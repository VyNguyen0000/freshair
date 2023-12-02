package com.example.afinal.api;

import com.example.afinal.model.PasswordResetRequest;
import com.example.afinal.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface CallUserId {
    @GET("api/master/user/user")
    Call<UserResponse> getUserId(
        @Header("Authorization") String authorization
    );
}
