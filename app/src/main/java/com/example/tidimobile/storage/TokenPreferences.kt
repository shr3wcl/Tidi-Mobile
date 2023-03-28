/**
 * Phạm Minh Trí VKU
 */
package com.example.tidimobile.storage

import android.content.Context
import android.content.SharedPreferences


class TokenPreferences(context: Context) {
    private val SHARED_PREF_NAME = "TokenPref"
    private var preferences: SharedPreferences? = null

    init {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String?) {
        val editor = preferences!!.edit()
        editor.putString("token", token)
        editor.apply()
    }

    fun getToken(): String? {
        return preferences!!.getString("token", "")
    }
}