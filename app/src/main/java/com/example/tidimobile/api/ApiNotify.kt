package com.example.tidimobile.api

import com.example.tidimobile.model.NotifyModel
import com.example.tidimobile.model.NotifyStoreModel
import com.example.tidimobile.model.ResponseMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiNotify {
    @GET("/v1/user/notify/get")
    fun getNotify(@Header("token") token: String): Call<NotifyModel>

    @POST("/v1/user/notify/store/{idUser}")
    fun storeNotify(@Path("idUser") idUser: String, @Header("token") token: String, @Body data: NotifyStoreModel): Call<ResponseMessage>
}