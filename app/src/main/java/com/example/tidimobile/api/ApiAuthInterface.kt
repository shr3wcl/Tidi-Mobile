package com.example.tidimobile.api

import com.example.tidimobile.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiAuthInterface {
    @POST("/v1/auth/register")
    fun registerUser(@Body data: UserRegisterModel): Call<ResponseMessage>

    @POST("/v1/auth/login")
    fun loginUser(@Body data: UserLoginModel): Call<UserLoginResponseModel>

    @POST("/v1/auth/logout")
    fun logoutUser(@Header("token") authToken: String): Call<ResponseMessage>

    @POST("/v1/auth/refresh")
    fun refreshToken(@Header("token") authToken: String): Call<TokenModel>
}