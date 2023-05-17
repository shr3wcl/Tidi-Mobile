package com.example.tidimobile.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.tidimobile.R
import com.example.tidimobile.api.ApiAuthInterface
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.FragmentLoginBinding
import com.example.tidimobile.model.*
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


class LoginFragment : Fragment() {
    private lateinit var service: ApiAuthInterface
    private lateinit var appPrefs: TokenPreferences
    private lateinit var userPrefs: UserPreferences
    private lateinit var blogService: ApiBlogInterface
    private lateinit var binding: FragmentLoginBinding
    private lateinit var menu: Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navView: NavigationView = requireActivity().findViewById(R.id.navView)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Login"
        }
        menu = navView.menu

        menu.findItem(R.id.item1).title = "Login"
        menu.findItem(R.id.item2).title = "Register"
        menu.findItem(R.id.item3).title = ""
        menu.findItem(R.id.item4).title = ""
        menu.findItem(R.id.item4).isEnabled = false
        menu.findItem(R.id.item3).isEnabled = false

        menu.findItem(R.id.item2).setOnMenuItemClickListener {
            callbackRegister()
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        service = ApiClient.getService()
        blogService = ApiClient.getBlog()
        appPrefs = TokenPreferences(inflater.context)
        userPrefs = UserPreferences(inflater.context)

        binding.btnLogin.setOnClickListener {
            loginHandle()
        }
        binding.textToRegister.setOnClickListener {
            callbackRegister()
        }
        return binding.root
    }

    private fun callbackRegister() {
        parentFragmentManager.beginTransaction().hide(this@LoginFragment)
            .add(R.id.fragment_container_user, RegisterFragment.newInstance())
            .commit()
    }

    private fun loginHandle() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        val username = binding.edtUsername.text.toString()
        val password = binding.edtPassword.text.toString()
        if (username.isEmpty()) {
            binding.edtUsername.error = "Username invalidate"
            return
        } else if (password.isEmpty()) {
            binding.edtPassword.error = "Password invalidate"
            return
        }

        val user = UserLoginModel(
            username, password
        )
        val call = service.loginUser(user)
        call.enqueue(object : Callback<UserLoginResponseModel> {
            override fun onResponse(
                call: Call<UserLoginResponseModel>,
                response: Response<UserLoginResponseModel>
            ) {
                if (response.isSuccessful) {
                    // Save Token
                    appPrefs.saveToken(
                        response.body()?.token?.accessToken.toString(),
                        response.body()?.token?.refreshToken.toString()
                    )
                    response.body()?.user?.let { userPrefs.saveInfoUser(it) }
                    parentFragmentManager.beginTransaction().hide(this@LoginFragment).add(
                        R.id.fragment_container_user,
                        UserInfoFragment.newInstance()
                    ).commit()
                } else {
                    var msgRes = response.errorBody()?.string()
                    val gson = Gson()
                    msgRes = gson.fromJson(msgRes, UserLoginResponseModel::class.java).message
                    binding.edtUsername.error = msgRes
                }
            }

            override fun onFailure(call: Call<UserLoginResponseModel>, t: Throwable) {
                if (t is HttpException && t.code() == 401) {
                    val errorMessage = t.response()?.errorBody()?.string()
                    binding.edtUsername.error = errorMessage
                } else {
                    binding.edtUsername.error = t.message.toString()
                }

            }


        })
    }

    companion object {
        fun newInstance(): LoginFragment = LoginFragment()
    }

}