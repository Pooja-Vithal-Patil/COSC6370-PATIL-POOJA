package com.example.weatherwardrobe.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("predict")
    @FormUrlEncoded
    Call<HashMap<String, String>> predict(@Field("firebaseImageID") String firebaseImageID);

}
