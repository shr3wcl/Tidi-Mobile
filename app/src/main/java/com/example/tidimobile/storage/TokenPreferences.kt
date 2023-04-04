package com.example.tidimobile.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences


class TokenPreferences(context: Context) {
    private val sharedPreferences = "TokenPref"
    private var preferences: SharedPreferences? = null

    init {
        preferences = context.getSharedPreferences(sharedPreferences, Context.MODE_PRIVATE)
    }

    fun saveToken(accessToken: String?, refreshToken: String?) {
        val editor = preferences!!.edit()
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun refreshAccessToken(accessToken: String?) {
        preferences!!.edit().putString("accessToken", accessToken).apply()
    }

    fun getToken(): String? {
        return preferences!!.getString("accessToken", "")
    }

    fun getRefreshToken(): String? {
        return preferences!!.getString("refreshToken", "")
    }

    @SuppressLint("CommitPrefEdits")
    fun clearToken(){
        preferences!!.edit().clear().apply()
    }
}