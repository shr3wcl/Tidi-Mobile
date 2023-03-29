package com.example.tidimobile.fragment

import android.os.Bundle
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
import com.example.tidimobile.storage.TokenPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserInfoFragment : Fragment() {
    private lateinit var service: ApiAuthInterface
    private lateinit var appPrefs: TokenPreferences
    private lateinit var binding: FragmentUserInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserInfoBinding.inflate(layoutInflater)
        service = ApiClient.getService()
        appPrefs = TokenPreferences(inflater.context)
        binding.btnLogout.setOnClickListener {
            logout()
        }
        return binding.root
    }
    private fun logout() {
        val call = service.logoutUser("Bearer ${appPrefs.getToken()}")
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT)
                    .show()
                appPrefs.clearToken()
                parentFragmentManager.beginTransaction().hide(this@UserInfoFragment).add(R.id.fragment_container_user, LoginFragment.newInstance()).commit()
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
    companion object {
        fun newInstance(): UserInfoFragment = UserInfoFragment()
    }
}