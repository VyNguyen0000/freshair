package com.example.afinal.api;

import com.example.afinal.model.PointWeather;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CallGraph {
    @POST("api/master/asset/datapoint/{assetId}/attribute/{attribute}")
    Call<List<PointWeather>> callGraph(
        @Path("assetId") String assetId,
        @Path("attribute") String attribute,
        @Header("Authorization") String authorization,
        @Body RequestBody requestBody
    );
}
