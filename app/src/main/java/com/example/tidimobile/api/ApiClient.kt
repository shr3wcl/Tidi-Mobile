package com.example.tidimobile.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        private const val BASE_URL = "https://278b-14-250-222-180.ngrok-free.app"
        private var retrofit: Retrofit? = null

        fun getService(): ApiAuthInterface {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit!!.create(ApiAuthInterface::class.java)
        }

        fun getBlog(): ApiBlogInterface {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit!!.create(ApiBlogInterface::class.java)
        }

        fun getUser(): ApiUserInterface{
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit!!.create(ApiUserInterface::class.java)
        }
    }
}