package com.example.tidimobile.api

import com.example.tidimobile.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiBlogInterface {
    // Blog
    @GET("/v1/user/blogs/basic/all")
    fun getAllBlogOfaUser(@Header("token") authHeader: String): Call<BlogModelBasic>

    @GET("/v1/user/blogs/public/basic/all")
    fun getAllBlogPublicUser(): Call<BlogModelBasic>

    @GET("/v1/user/blogs/{idBlog}")
    fun getDetailBlog(@Path("idBlog") idBlog: String): Call<BlogModelDetail>

    @POST("/v1/user/blogs/store")
    fun saveNewBlog(
        @Header("token") authToken: String,
        @Body data: BlogNewModel
    ): Call<ResponseMessage>

    @POST("/v1/user/blogs/edit/{idBlog}")
    fun editBlog(@Header("token") authToken: String, @Body data: BlogModel.BlogObject, @Path("idBlog") idBlog: String): Call<ResponseMessage>

    @POST("/v1/user/blogs/like/{idBlog}")
    fun increaseLikeBlog(
        @Header("token") authToken: String,
        @Path("idBlog") idBlog: String
    ): Call<ResponseMessage>

    @DELETE("/v1/user/blogs/delete/{idBlog}")
    fun deleteBlog(
        @Header("token") authToken: String,
        @Path("idBlog") idBlog: String
    ): Call<ResponseMessage>

    @POST("/v1/user/blogs/search")
    fun searchBlog(@Body keySearch: String): Call<List<BlogModelBasic>>

    // Comment

    @GET("/v1/user/blogs/comment/{idBlog}")
    fun getComments(@Path("idBlog") idBlog: String): Call<List<CommentModel>>

    @POST("/v1/user/comment/add/{idBlog}")
    fun addNewComment(
        @Header("token") authToken: String,
        @Path("idBlog") idBlog: String
    ): Call<ResponseMessage>

    @DELETE("/v1/user/comment/{idComment}")
    fun deleteComment(
        @Header("token") authToken: String,
        @Path("idComment") idComment: String
    ): Call<ResponseMessage>

    @POST("/v1/user/comment/edit/{idComment}")
    fun editComment(
        @Header("token") authToken: String,
        @Path("idComment") idComment: String
    ): Call<ResponseMessage>
}