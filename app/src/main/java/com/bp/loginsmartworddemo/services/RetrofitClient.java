package com.bp.loginsmartworddemo.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

class RetrofitClient {
    static Retrofit getClient(String baseUrl){
        OkHttpClient builder = new OkHttpClient.Builder()
                                            .readTimeout(10000, TimeUnit.MILLISECONDS)
                                            .writeTimeout(5000, TimeUnit.MILLISECONDS)
                                            .connectTimeout(10000, TimeUnit.MILLISECONDS)
                                            .retryOnConnectionFailure(true)
                                            .build();

        Gson gson = new GsonBuilder().setLenient().create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }
}
