package com.example.tidimobile.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tidimobile.R
import com.example.tidimobile.api.ApiAuthInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.FragmentUserInfoBinding
import com.example.tidimobile.model.ResponseMessage
import com.example.tidimobile.model.UserLoginResponseModel
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserInfoFragment() :
    Fragment() {
    private lateinit var service: ApiAuthInterface
    private lateinit var tokenPrefs: TokenPreferences
    private lateinit var userPrefs: UserPreferences
    private lateinit var binding: FragmentUserInfoBinding
    private lateinit var userCurrent: UserLoginResponseModel.UserLoginObject
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(layoutInflater)
        service = ApiClient.getService()
        tokenPrefs = TokenPreferences(inflater.context)
        userPrefs = UserPreferences(inflater.context)
        val idUser = arguments?.getString("idUserToGetInfo")
        if(idUser?.isEmpty() == true || idUser == null){
            getCurrentUser()
        }else{
            getUserById(idUser)
        }
        binding.btnLogout.setOnClickListener {
            logout()
        }
        return binding.root
    }

    private fun getUserById(idUser: String) {
        TODO("Not yet implemented")
    }

    private fun getCurrentUser() {
        userCurrent = userPrefs.getInfoUser()
        binding.imageViewAvatar.setImageBitmap(userCurrent.avatar?.let { base64ToBitmap(it) })
        binding.txtNameProfile.text = "${userCurrent.firstName} ${userCurrent.lastName}"
        Toast.makeText(context, userCurrent._id.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun logout() {
        val call = service.logoutUser("Bearer ${tokenPrefs.getToken()}")
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT)
                    .show()
                tokenPrefs.clearToken()
                userPrefs.clearInfo()
                parentFragmentManager.beginTransaction().hide(this@UserInfoFragment)
                    .add(R.id.fragment_container_user, LoginFragment.newInstance()).commit()
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun base64ToBitmap(base64String: String): Bitmap? {
        val decodedString = Base64.decode(base64String.split(",")[1], Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
    companion object {
        fun newInstance(): UserInfoFragment = UserInfoFragment()
    }
}