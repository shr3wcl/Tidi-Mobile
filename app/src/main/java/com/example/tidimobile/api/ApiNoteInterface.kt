package com.example.tidimobile.api

import com.example.tidimobile.model.NoteModel
import com.example.tidimobile.model.ResponseMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiNoteInterface {
    @GET("/v1/user/project/{idProject}/note/getall")
    fun getAllNote(
        @Header("token") authToken: String,
        @Path("idProject") idProject: String
    ): Call<List<NoteModel>>

    @POST("/v1/user/project/{idProject}/note/add")
    fun addNewNote(
        @Header("token") authToken: String,
        @Path("idProject") idProject: String,
        @Body note: NoteModel
    ): Call<ResponseMessage>

    @DELETE("/v1/user/project/note/delete/{idNote}")
    fun deleteNote(
        @Header("token") authToken: String,
        @Path("idNote") idNote: String
    ): Call<ResponseMessage>

    @POST("/v1/user/project/note/edit/{idNote}")
    fun editNote(
        @Header("token") authToken: String,
        @Path("idNote") idNote: String,
        @Body note: NoteModel
    ): Call<ResponseMessage>

    @GET("/v1/user/project/note/detail/{idNote}")
    fun getDetailNote(
        @Header("token") authToken: String,
        @Path("idNote") idNote: String
    ): Call<NoteModel>
}