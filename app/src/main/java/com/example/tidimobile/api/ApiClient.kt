package com.example.tidimobile.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ApiClient {
    companion object {
        private var BASE_URL = Url().url
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

        fun getNotify(): ApiNotify{
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit!!.create(ApiNotify::class.java)
        }

        fun getSearch(): ApiSearchInterface{
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit!!.create(ApiSearchInterface::class.java)
        }
    }
}