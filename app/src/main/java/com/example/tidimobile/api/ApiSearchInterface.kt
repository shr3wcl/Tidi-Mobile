package com.example.tidimobile.api

import com.example.tidimobile.model.BlogModelBasic
import com.example.tidimobile.model.SearchKeyModel
import com.example.tidimobile.model.SearchModel
import com.example.tidimobile.model.UserSearchModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiSearchInterface {
    @GET("/v1/user/blogs/get4search")
    fun get4Search(): Call<SearchModel>

    @POST("/v1/user/blogs/search")
    fun search(@Body key: SearchKeyModel): Call<BlogModelBasic>

    @POST("/v1/user/users/search")
    fun searchUser(@Body key: SearchKeyModel): Call<UserSearchModel>
}