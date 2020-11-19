package com.example.lab12_ieti;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("authetication")
    public Call<Token> login(@Body LoginWrapper login);
}