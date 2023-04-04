package com.example.tidimobile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tidimobile.R
import com.example.tidimobile.api.ApiAuthInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.FragmentRegisterBinding
import com.example.tidimobile.model.ResponseMessage
import com.example.tidimobile.model.UserRegisterModel
import com.example.tidimobile.storage.TokenPreferences
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {
    private lateinit var service: ApiAuthInterface
    private lateinit var appPrefs: TokenPreferences
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var menu: Menu
    private val emailRegex = Regex("^([\\w.\\-]+)@([\\w\\-]+)((\\.(\\w){2,3})+)$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menu = requireActivity().findViewById<NavigationView?>(R.id.navView).menu
        menu.findItem(R.id.item1).setOnMenuItemClickListener{
            callbackLogin()
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        service = ApiClient.getService()
        appPrefs = TokenPreferences(inflater.context)
        binding.registerBtn.setOnClickListener {
            registerHandle()
        }
        binding.returnLogin.setOnClickListener {
            callbackLogin()
        }
        return binding.root
    }

    private fun callbackLogin() {
        parentFragmentManager.beginTransaction().hide(this@RegisterFragment)
            .add(R.id.fragment_container_user, LoginFragment.newInstance())
            .commit()

    }

    private fun registerHandle() {
        val firstName = binding.edtFirstName.text.toString()
        val lastName = binding.edtLastName.text.toString()
        val username = binding.edtUsername.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val gender = if (binding.genderMale.isChecked) {
            "Male"
        } else if (binding.genderFemale.isChecked) {
            "Female"
        } else {
            "Others"
        }

        if (firstName.isEmpty()) {
            binding.edtFirstName.error = "Empty"
            return
        }

        if (lastName.isEmpty()) {
            binding.edtLastName.error = "Empty"
            return
        }
        if (username.isEmpty() || username.length < 6) {
            binding.edtUsername.error = "Username must have 6 character"
            return
        }
        if (email.isEmpty() || !email.matches(emailRegex)) {
            binding.edtEmail.error = "Email invalidate"
            return
        }
        if (password.isEmpty() || password.length < 6) {
            binding.edtPassword.error = "Password must have 6 character"
            return
        }
        if (binding.edtPassword.text.toString() != binding.edtPasswordType.text.toString()) {
            binding.edtPasswordType.error = "Mismatched"
            return
        }

        val user = UserRegisterModel(
            firstName, lastName, username, email, gender, password
        )
        val call = service.registerUser(user)
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()

            }

        })
    }

    companion object {
        fun newInstance(): RegisterFragment = RegisterFragment()
    }
}