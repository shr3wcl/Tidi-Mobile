package com.example.tidimobile.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.example.tidimobile.model.UserLoginResponseModel

class UserPreferences(context: Context) {
    private val SHARED_PREF_NAME = "UserPref"
    private var preferences: SharedPreferences? = null

    init {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveInfoUser(userObject: UserLoginResponseModel.UserLoginObject) {
        val editor = preferences!!.edit()
        editor.putString("firstName", userObject.firstName)
        editor.putString("lastName", userObject.lastName)
        editor.putString("email", userObject.email)
        editor.putString("gender", userObject.gender)
        editor.putString("id", userObject._id)
        editor.putString("dayJoined", userObject.createdAt)
        editor.putString("avatar", userObject.avatar)
        editor.putString("username", userObject.username)
        userObject.admin?.let { editor.putBoolean("admin", it) }

        editor.apply()
    }

    fun getInfoUser(): UserLoginResponseModel.UserLoginObject {
        return UserLoginResponseModel.UserLoginObject(
            preferences!!.getString("id", ""),
            preferences!!.getString("firstName", ""),
            preferences!!.getString("lastName", ""),
            preferences!!.getString("username", ""),
            preferences!!.getString("email", ""),
            preferences!!.getString("gender", ""),
            preferences!!.getBoolean("admin", false),
            preferences!!.getString("avatar", ""),
            preferences!!.getString("dayJoined", ""),
            )
    }

    @SuppressLint("CommitPrefEdits")
    fun clearInfo(){
        preferences!!.edit().clear().apply()
    }
}