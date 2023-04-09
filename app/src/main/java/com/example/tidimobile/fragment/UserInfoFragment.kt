package com.example.tidimobile.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.SpannableString
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tidimobile.BlogDetailActivity
import com.example.tidimobile.EditProfileActivity
import com.example.tidimobile.R
import com.example.tidimobile.adapter.BlogsAdapter
import com.example.tidimobile.api.ApiAuthInterface
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.FragmentUserInfoBinding
import com.example.tidimobile.model.BlogModelBasic
import com.example.tidimobile.model.ResponseMessage
import com.example.tidimobile.model.UserLoginResponseModel
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserInfoFragment :
    Fragment() {
    private lateinit var blogService: ApiBlogInterface
    private lateinit var service: ApiAuthInterface
    private lateinit var tokenPrefs: TokenPreferences
    private lateinit var userPrefs: UserPreferences
    private lateinit var binding: FragmentUserInfoBinding
    private lateinit var userCurrent: UserLoginResponseModel.UserLoginObject
    private lateinit var listBlog: ArrayList<BlogModelBasic.BlogBasicObject>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navView: NavigationView = requireActivity().findViewById(R.id.navView)
        val menu: Menu = navView.menu

        menu.findItem(R.id.item1).title = "Edit Profile"
        menu.findItem(R.id.item2).title = "My Blog"
        menu.findItem(R.id.item3).title = "New Blog"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(layoutInflater)
        service = ApiClient.getService()
        tokenPrefs = TokenPreferences(inflater.context)
        userPrefs = UserPreferences(inflater.context)
        blogService = ApiClient.getBlog()
        val idUser = arguments?.getString("idUserToGetInfo")
        if(idUser?.isEmpty() == true || idUser == null){
            getCurrentUser()
        }else{
            getUserById(idUser)
        }
        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.editProfileIcon.setOnClickListener{
            startActivity(Intent(context, EditProfileActivity::class.java))
        }
        return binding.root
    }

    private fun getUserById(idUser: String) {
        print(idUser)
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentUser() {
        userCurrent = userPrefs.getInfoUser()
        binding.imageViewAvatar.setImageBitmap(userCurrent.avatar?.let { base64ToBitmap(it) })
        binding.txtNameProfile.text = "${userCurrent.firstName} ${userCurrent.lastName}"
        getOwnerBlog()
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

    private fun getOwnerBlog(){
        binding.rcViewListBlog.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        val call = blogService.getAllBlogOfaUser("Bearer ${tokenPrefs.getToken()}")
        call.enqueue(object : Callback<BlogModelBasic> {
            override fun onResponse(
                call: Call<BlogModelBasic>,
                response: Response<BlogModelBasic>
            ) {
                if(response.isSuccessful){
                    listBlog = response.body()?.blogs!!
                    binding.rcViewListBlog.layoutManager = LinearLayoutManager(requireContext())
                    val bAdapter = BlogsAdapter(listBlog)
                    bAdapter.setOnItemClickListener(object : BlogsAdapter.OnBlogClickListener{
                        override fun onClickBlog(position: Int) {
                            val intent = Intent(context, BlogDetailActivity::class.java)
                            intent.putExtra("id", listBlog[position]._id)
                            intent.putExtra("title", listBlog[position].title)
                            intent.putExtra("author", "${listBlog[position].idUser?.firstName} ${listBlog[position].idUser?.lastName}")
                            startActivity(intent)
                        }

                    })
                    binding.rcViewListBlog.adapter = bAdapter
                }
                binding.rcViewListBlog.visibility = View.VISIBLE
                binding.tvLoading.visibility = View.GONE
            }

            override fun onFailure(call: Call<BlogModelBasic>, t: Throwable) {
                Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT).show()
            }

        })
    }
    companion object {
        fun newInstance(): UserInfoFragment = UserInfoFragment()
    }
}