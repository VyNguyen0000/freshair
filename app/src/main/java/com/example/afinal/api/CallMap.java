package com.example.afinal.api;

import com.example.afinal.model.MapResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CallMap {
    @GET("api/master/map/js")
    Call<MapResponse> getMapData(
        @Header("Authorization") String authorization
    );
}
