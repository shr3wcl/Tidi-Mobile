/**
 * Phạm Minh Trí VKU
 */
package com.example.tidimobile.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        private const val BASE_URL = "https://6661-14-250-222-180.ap.ngrok.io"
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
    }
}