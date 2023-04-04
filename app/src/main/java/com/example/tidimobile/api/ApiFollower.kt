package com.example.tidimobile.api

import com.example.tidimobile.model.FollowModelAdd
import com.example.tidimobile.model.FollowModelCheck
import com.example.tidimobile.model.FollowModelGet
import com.example.tidimobile.model.ResponseMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiFollower {
    @GET("/v1/user/follow/all/{idUser}")
    fun getAll(@Path("idUser") idUser: String): Call<FollowModelGet>

    @POST("/v1/user/follow/add/{idUser}")
    fun follow(@Path("idUser") idUser: String, @Header("token") token: String, @Body formBody: FollowModelAdd): Call<ResponseMessage>

    @POST("/v1/user/follow/delete")
    fun unfollow(@Header("token") token: String, @Body formBody: FollowModelAdd): Call<ResponseMessage>

    @POST("/v1/user/follow/check")
    fun checkFollow(@Header("token") token: String, @Body formBody: FollowModelAdd): Call<FollowModelCheck>
}