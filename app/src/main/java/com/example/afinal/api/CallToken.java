package com.example.afinal.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import com.example.afinal.model.ResponseModel;

public interface CallToken {
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("auth/realms/master/protocol/openid-connect/token")
    Call<ResponseModel> sendRequest(
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId,
            @Field("username") String username,
            @Field("password") String password
    );
}
