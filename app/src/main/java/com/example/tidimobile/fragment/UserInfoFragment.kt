package com.example.tidimobile.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tidimobile.BlogDetailActivity
import com.example.tidimobile.EditProfileActivity
import com.example.tidimobile.InfoUserActivity
import com.example.tidimobile.R
import com.example.tidimobile.adapter.BlogsAdapter
import com.example.tidimobile.adapter.FollowAdapter
import com.example.tidimobile.adapter.FollowingApdater
import com.example.tidimobile.api.ApiAuthInterface
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.api.ApiFollower
import com.example.tidimobile.databinding.FragmentUserInfoBinding
import com.example.tidimobile.model.*
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
    private lateinit var listFollower: ArrayList<FollowModelGet.FollowersData>
    private lateinit var listFollowing: ArrayList<FollowingModelGet.FollowersData>
    private lateinit var followService: ApiFollower


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navView: NavigationView = requireActivity().findViewById(R.id.navView)
        val menu: Menu = navView.menu
        blogService = ApiClient.getBlog()
        followService = ApiClient.getFollow()
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "User Information"
        }
        menu.findItem(R.id.item1).title = "Edit Profile"
        menu.findItem(R.id.item2).title = "My Blog"
        menu.findItem(R.id.item3).title = "New Blog"
        menu.findItem(R.id.item4).title = "Logout"
        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.item1 -> {startActivity(Intent(context, EditProfileActivity::class.java))}
                R.id.item2 -> {}
                R.id.item3 -> {}
                R.id.item4 -> logout()
            }
            true
        }

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
        binding.tvBlogBtn.setOnClickListener {
            binding.tvBlogBtn.setBackgroundColor(Color.GRAY)
            binding.tvFollowerBtn.setBackgroundColor(Color.WHITE)
            binding.tvFollowingBtn.setBackgroundColor(Color.WHITE)
            getOwnerBlog()
        }
        binding.tvFollowingBtn.setOnClickListener {
            binding.tvBlogBtn.setBackgroundColor(Color.WHITE)
            binding.tvFollowerBtn.setBackgroundColor(Color.WHITE)
            binding.tvFollowingBtn.setBackgroundColor(Color.GRAY)
            getDataFollowing()
        }
        binding.tvFollowerBtn.setOnClickListener {
            binding.tvBlogBtn.setBackgroundColor(Color.WHITE)
            binding.tvFollowerBtn.setBackgroundColor(Color.GRAY)
            binding.tvFollowingBtn.setBackgroundColor(Color.WHITE)
            getDataFollower()
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
        try {
            val imageBytes = Base64.decode(userCurrent.avatar, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            Glide.with(this).load(decodedImage).diskCacheStrategy(
                DiskCacheStrategy.ALL).circleCrop().into(binding.imageViewAvatar)
        }catch (e: java.lang.Exception){
            Toast.makeText(context, "Cannot load image now", Toast.LENGTH_SHORT).show()
        }
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
//    private fun base64ToBitmap(base64String: String): Bitmap? {
//        val decodedString = Base64.decode(base64String.split(",")[1], Base64.DEFAULT)
//        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//    }

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
                            intent.putExtra("description", listBlog[position].description)
                            intent.putExtra(
                                "author",
                                "${listBlog[position].idUser?.firstName} ${listBlog[position].idUser?.lastName}"
                            )
                            intent.putExtra("idUser", listBlog[position].idUser?._id)
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


    private fun getDataFollower() {
        binding.rcViewListBlog.visibility = View.GONE
        binding.rcViewListFollowing.visibility = View.GONE
        binding.rcViewListFollower.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        val call = followService.getAll(userCurrent._id.toString())
        call.enqueue(object : Callback<FollowModelGet>{
            override fun onResponse(
                call: Call<FollowModelGet>,
                response: Response<FollowModelGet>
            ) {
                if(response.isSuccessful){
                    listFollower = response.body()?.followers!!
                    binding.rcViewListFollower.layoutManager = LinearLayoutManager(context)
                    val ferAdapter = FollowAdapter(listFollower)
                    ferAdapter.setOnItemClickListener(object : FollowAdapter.OnFlwClickListener{
                        override fun onClickFlw(position: Int) {
                            val intentUser =
                                Intent(context, InfoUserActivity::class.java)
                            intentUser.putExtra("idUserBlog", listFollower[position].idFollow?._id.toString())
                            intentUser.putExtra(
                                "name",
                                listFollower[position].idFollow?.firstName.toString() + " " + listFollower[position].idFollow?.lastName.toString()
                            )
                            startActivity(intentUser)
                        }
                    })
                    binding.rcViewListFollower.adapter = ferAdapter

                }else{
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
                binding.rcViewListFollower.visibility = View.VISIBLE
                binding.tvLoading.visibility = View.GONE
            }

            override fun onFailure(call: Call<FollowModelGet>, t: Throwable) {
                Toast.makeText(context, "Error, please try again later!", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun getDataFollowing() {
        binding.rcViewListBlog.visibility = View.GONE
        binding.rcViewListFollower.visibility = View.GONE
        binding.rcViewListFollowing.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        val call = followService.getAllFollowing(userCurrent._id.toString())
        call.enqueue(object : Callback<FollowingModelGet>{
            override fun onResponse(
                call: Call<FollowingModelGet>,
                response: Response<FollowingModelGet>
            ) {
                if(response.isSuccessful){
                    listFollowing = response.body()?.followers!!
                    binding.rcViewListFollowing.layoutManager = LinearLayoutManager(context)
                    val ferAdapter = FollowingApdater(listFollowing)
                    ferAdapter.setOnItemClickListener(object : FollowingApdater.OnFlwClickListener{
                        override fun onClickFlw(position: Int) {
                            val intentUser =
                                Intent(context, InfoUserActivity::class.java)
                            intentUser.putExtra("idUserBlog", listFollowing[position].idUser?._id.toString())
                            intentUser.putExtra(
                                "name",
                                listFollowing[position].idUser?.firstName.toString() + " " + listFollowing[position].idUser?.lastName.toString()
                            )
                            startActivity(intentUser)
                        }

                    })
                    binding.rcViewListFollowing.adapter = ferAdapter

                }else{
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
                binding.rcViewListFollowing.visibility = View.VISIBLE
                binding.tvLoading.visibility = View.GONE
            }

            override fun onFailure(call: Call<FollowingModelGet>, t: Throwable) {
                Toast.makeText(context, "Error, please try again later!", Toast.LENGTH_SHORT).show()

            }
        })
    }
    companion object {
        fun newInstance(): UserInfoFragment = UserInfoFragment()
    }
}