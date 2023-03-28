/**
 * Phạm Minh Trí VKU
 */
package com.example.tidimobile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tidimobile.api.ApiAuthInterface
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.ActivityMainBinding
import com.example.tidimobile.model.*
import com.example.tidimobile.storage.TokenPreferences
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var service: ApiAuthInterface
    private lateinit var blogService: ApiBlogInterface
    private lateinit var appPrefs: TokenPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        service = ApiClient.getService()
        blogService = ApiClient.getBlog()
        appPrefs = TokenPreferences(applicationContext)
        binding.registerBtn.setOnClickListener {
            registerHandle()
        }

        binding.btnLogin.setOnClickListener {
            loginHandle()
        }

        binding.btnGet.setOnClickListener {
            getAllBlog()
        }
    }

    private fun getAllBlog() {
        val call = blogService.getAllBlogOfaUser("Bearer ${appPrefs.getToken()}")
        call.enqueue(object : Callback<BlogModelBasic> {
            override fun onResponse(
                call: Call<BlogModelBasic>,
                response: Response<BlogModelBasic>
            ) {
                if (response.isSuccessful) {
                    val a = response.body()?.blogs
                    var b = "Null"
                    if (a != null) {
                        if (a.isNotEmpty()) {
                            b = a[0]._id.toString()
                        }
                    }
                    Toast.makeText(this@MainActivity, b, Toast.LENGTH_SHORT).show()
                } else {
                    var a = response.errorBody()?.string()
                    val gson = Gson()
                    a = gson.fromJson(a, ResponseMessage::class.java).message
                    Toast.makeText(this@MainActivity, a, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BlogModelBasic>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Lỗi tào lao hơn", Toast.LENGTH_SHORT).show()

            }

        })
    }

    private fun loginHandle() {
        val user = UserLoginModel(
            binding.edtUsername.text.toString(),
            binding.edtPassword.text.toString()
        )
        val call = service.loginUser(user)
        call.enqueue(object : Callback<UserLoginResponseModel> {
            override fun onResponse(
                call: Call<UserLoginResponseModel>,
                response: Response<UserLoginResponseModel>
            ) {
                if (response.isSuccessful) {

                    appPrefs.saveToken(response.body()?.token?.accessToken.toString())
                    Toast.makeText(
                        this@MainActivity,
                        response.body()?.token?.accessToken.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    var a = response.errorBody()?.string()
                    val gson = Gson()
                    a = gson.fromJson(a, UserLoginResponseModel::class.java).message

                    Toast.makeText(this@MainActivity, a, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserLoginResponseModel>, t: Throwable) {
                if (t is HttpException && t.code() == 401) {
                    // handle unauthorized error
                    // retrieve the error message from the response body
                    val errorMessage = t.response()?.errorBody()?.string()
                    // show error message to user or redirect to login page
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    // handle other errors
                    Toast.makeText(this@MainActivity, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

            }


        })
    }

    private fun registerHandle() {
        val user = UserRegisterModel(
            binding.edtFirstName.text.toString(),
            binding.edtLastName.text.toString(),
            binding.edtUsername.text.toString(),
            binding.edtEmail.text.toString(),
            binding.edtGender.text.toString(),
            binding.edtPassword.text.toString()
        )
        val call = service.registerUser(user)
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failure", Toast.LENGTH_SHORT).show()

            }

        })
    }
}