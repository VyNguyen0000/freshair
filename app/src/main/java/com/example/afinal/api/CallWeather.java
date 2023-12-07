package com.example.afinal.api;

import com.example.afinal.model.PasswordResetRequest;
import com.example.afinal.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CallWeather {
    @GET("api/master/asset/{assetId}")
    Call<WeatherResponse> callWeather(
        @Path("assetId") String assetId,
        @Header("Authorization") String authorization
    );
}
