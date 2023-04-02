package com.example.tidimobile.api

import com.example.tidimobile.model.NotifyStoreModel
import com.example.tidimobile.model.ResponseMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiNotify {
    @GET("/v1/user/notify/{idUser}")
    fun getNotify(@Path("idUser") idUser: String, @Header("token") token: String): Call<ResponseMessage>

    @POST("/v1/user/notify/{idUser}/store")
    fun storeNotify(@Path("idUser") idUser: String, @Header("token") token: String, @Body data: NotifyStoreModel): Call<ResponseMessage>
}