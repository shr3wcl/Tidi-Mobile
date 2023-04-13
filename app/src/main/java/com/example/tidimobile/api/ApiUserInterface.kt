package com.example.tidimobile.api

import com.example.tidimobile.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiUserInterface {
    @GET("/v1/user/info/{idUser}")
    fun getInfoUser(@Path("idUser") idUser: String): Call<UserLoginResponseModel>

    @POST("/v1/user/edit/{idUser}")
    fun changeInfoUser(
        @Path("idUser") idUser: String,
        @Header("token") authToken: String,
        @Body data: UserChangedModel
    ): Call<UserEditResponse>

    @POST("/v1/user/change/password")
    fun changePassword(@Header("token") authToken: String, @Body data: UserChangePwdModel): Call<ResponseMessage>

    @POST("/v1/user/change/avatar")
    fun changeAvatar(@Header("token") authToken: String, @Body data: AvatarModel): Call<ResponseMessage>
}