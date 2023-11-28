package com.example.afinal.api;

import com.example.afinal.model.PasswordResetRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CallResetPwd {
    @PUT("api/master/user/master/reset-password/{uuid}")
    Call<Void> resetPassword(
        @Path("uuid") String uuid,
        @Header("Authorization") String authorization,
        @Body PasswordResetRequest requestBody
    );
}
