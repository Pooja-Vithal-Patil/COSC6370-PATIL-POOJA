package com.example.weatherwardrobe.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    //    private static final String Base_URL = AES.decrypt(AppConstant.BASE_URL);;
    private static Retrofit retrofit;
    private static final String Base_URL = "http://clothrecommendation.pythonanywhere.com/";

    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();


    public static Retrofit getApiClient() {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(Base_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}

