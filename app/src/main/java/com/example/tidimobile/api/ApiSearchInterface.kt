package com.example.tidimobile.api

import com.example.tidimobile.model.SearchModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiSearchInterface {
    @GET("/v1/user/blogs/get4search")
    fun get4Search(): Call<SearchModel>
}