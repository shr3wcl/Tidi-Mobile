package com.example.tidimobile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tidimobile.adapter.BlogsAdapter
import com.example.tidimobile.adapter.FollowAdapter
import com.example.tidimobile.adapter.FollowingAdapter
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.api.ApiFollower
import com.example.tidimobile.databinding.ActivityInfoUserBinding
import com.example.tidimobile.model.*
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class InfoUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoUserBinding
    private lateinit var idUser: String
    private lateinit var blogService: ApiBlogInterface
    private lateinit var followService: ApiFollower
    private lateinit var listBlog: ArrayList<BlogModelBasic.BlogBasicObject>
    private lateinit var listFollower: ArrayList<FollowModelGet.FollowersData>
    private lateinit var listFollowing: ArrayList<FollowingModelGet.FollowersData>
    private lateinit var userCurrent: UserInfoModel.UserModelEdit
    private var checkFollow by Delegates.notNull<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        blogService = ApiClient.getBlog()
        followService = ApiClient.getFollow()
        binding.txtNameProfile.text = intent.getStringExtra("name")
        title = intent.getStringExtra("name")
        idUser = intent.getStringExtra("idUserBlog").toString()
        getDataBlog()
        binding.btnFollow.setOnClickListener {
            if (binding.btnFollow.text == "Follow") {
                handleFollow()
            } else if (binding.btnFollow.text == "Unfollow") {
                handleUnFollow()
            }
        }
        binding.tvBlogBtn.setOnClickListener {
            binding.tvBlogBtn.setBackgroundColor(Color.GRAY)
            binding.tvFollowerBtn.setBackgroundColor(Color.WHITE)
            binding.tvFollowingBtn.setBackgroundColor(Color.WHITE)
            getDataBlog()
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
        if (TokenPreferences(applicationContext).getToken().toString()
                .isNotEmpty() && UserPreferences(applicationContext).getInfoUser()._id != idUser
        ) {
            binding.btnFollow.visibility = View.VISIBLE
            getCheckFollow()
        } else {
            binding.btnFollow.visibility = View.GONE
        }
        getDataFollower()
        getInfoUser()
    }

    private fun getInfoUser() {
        ApiClient.getUser().getInfoUser(idUser).enqueue(object : Callback<UserInfoModel> {
            override fun onResponse(
                call: Call<UserInfoModel>,
                response: Response<UserInfoModel>
            ) {
                if (response.isSuccessful) {
                    userCurrent = response.body()?.user!!
                    if (userCurrent.avatar.toString().isNotEmpty()) {
                        try {
                            val imageBytes = Base64.decode(userCurrent.avatar, Base64.DEFAULT)

                            val decodedImage =
                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            Glide.with(applicationContext).load(decodedImage).diskCacheStrategy(
                                DiskCacheStrategy.ALL
                            ).circleCrop().into(binding.imageViewAvatar)
                        } catch (e: java.lang.Exception) {
                            Toast.makeText(
                                applicationContext,
                                "Cannot load image now",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error, please try again later!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserInfoModel>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Error, please try again later!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })


    }

    private fun handleFollow() {
        val followForm = FollowModelAdd(UserPreferences(applicationContext).getInfoUser()._id)
        followService.follow(
            idUser,
            "Bearer ${TokenPreferences(applicationContext).getToken()}",
            followForm
        ).enqueue(object : Callback<ResponseMessage> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                    binding.btnFollow.text = "Unfollow"
                    val notify = NotifyStoreModel(
                        idUser,
                        "${intent.getStringExtra("name")} followed you",
                        "Follow",
                        UserPreferences(applicationContext).getInfoUser()._id,
                        ""
                    )
                    ApiClient.getNotify().storeNotify(
                        idUser,
                        "Bearer ${TokenPreferences(applicationContext).getToken()}",
                        notify
                    ).enqueue(object : Callback<ResponseMessage> {
                        override fun onResponse(
                            call: Call<ResponseMessage>,
                            response: Response<ResponseMessage>
                        ) {
                        }

                        override fun onFailure(
                            call: Call<ResponseMessage>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                applicationContext,
                                "Error 2",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Error, please try again later!",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })
    }

    private fun handleUnFollow() {
        val followForm =
            FollowModelAdd(UserPreferences(applicationContext).getInfoUser()._id.toString())
        followService.unfollow(
            "Bearer ${TokenPreferences(applicationContext).getToken()}",
            followForm
        ).enqueue(object : Callback<ResponseMessage> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                    binding.btnFollow.text = "Follow"
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Error, please try again later!",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })
    }

    private fun getDataFollower() {
        binding.rcViewListBlog.visibility = View.GONE
        binding.rcViewListFollowing.visibility = View.GONE
        binding.rcViewListFollower.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        val call = followService.getAll(idUser)
        call.enqueue(object : Callback<FollowModelGet> {
            override fun onResponse(
                call: Call<FollowModelGet>,
                response: Response<FollowModelGet>
            ) {
                if (response.isSuccessful) {
                    listFollower = response.body()?.followers!!
                    binding.rcViewListFollower.layoutManager =
                        LinearLayoutManager(applicationContext)
                    val ferAdapter = FollowAdapter(listFollower)
                    ferAdapter.setOnItemClickListener(object : FollowAdapter.OnFlwClickListener {
                        override fun onClickFlw(position: Int) {
                            val intentUser =
                                Intent(applicationContext, InfoUserActivity::class.java)
                            intentUser.putExtra(
                                "idUser",
                                listFollower[position].idFollow?._id.toString()
                            )
                            intentUser.putExtra(
                                "name",
                                listFollower[position].idFollow?.firstName.toString() + " " + listFollower[position].idFollow?.lastName.toString()
                            )
                            startActivity(intentUser)
                        }

                    })
                    binding.rcViewListFollower.adapter = ferAdapter

                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
                binding.rcViewListFollower.visibility = View.VISIBLE
                binding.tvLoading.visibility = View.GONE
            }

            override fun onFailure(call: Call<FollowModelGet>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Error, please try again later!",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }

    private fun getDataFollowing() {
        binding.rcViewListBlog.visibility = View.GONE
        binding.rcViewListFollower.visibility = View.GONE
        binding.rcViewListFollowing.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        val call = followService.getAllFollowing(idUser)
        call.enqueue(object : Callback<FollowingModelGet> {
            override fun onResponse(
                call: Call<FollowingModelGet>,
                response: Response<FollowingModelGet>
            ) {
                if (response.isSuccessful) {
                    listFollowing = response.body()?.followers!!
                    binding.rcViewListFollowing.layoutManager =
                        LinearLayoutManager(applicationContext)
                    val ferAdapter = FollowingAdapter(listFollowing)
                    ferAdapter.setOnItemClickListener(object : FollowingAdapter.OnFlwClickListener {
                        override fun onClickFlw(position: Int) {
                            val intentUser =
                                Intent(applicationContext, InfoUserActivity::class.java)
                            intentUser.putExtra(
                                "idUser",
                                listFollowing[position].idUser?._id.toString()
                            )
                            intentUser.putExtra(
                                "name",
                                listFollowing[position].idUser?.firstName.toString() + " " + listFollowing[position].idUser?.lastName.toString()
                            )
                            startActivity(intentUser)
                        }

                    })
                    binding.rcViewListFollowing.adapter = ferAdapter

                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
                binding.rcViewListFollowing.visibility = View.VISIBLE
                binding.tvLoading.visibility = View.GONE
            }

            override fun onFailure(call: Call<FollowingModelGet>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Error, please try again later!",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }

    private fun getDataBlog() {
        binding.rcViewListBlog.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        val call = blogService.getAllBlogOfUser(idUser)
        call.enqueue(object : Callback<BlogModelBasic> {
            override fun onResponse(
                call: Call<BlogModelBasic>,
                response: Response<BlogModelBasic>
            ) {
                if (response.isSuccessful) {
                    listBlog = response.body()?.blogs!!
                    binding.rcViewListBlog.layoutManager = LinearLayoutManager(applicationContext)
                    val bAdapter = BlogsAdapter(listBlog)
                    bAdapter.setOnItemClickListener(object : BlogsAdapter.OnBlogClickListener {
                        override fun onClickBlog(position: Int) {
                            val intent = Intent(applicationContext, BlogDetailActivity::class.java)
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
                Toast.makeText(
                    applicationContext,
                    "Error, please try again later!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun getCheckFollow() {
        val followForm = FollowModelAdd(idUser)
        val call = followService.checkFollow(
            "Bearer ${
                TokenPreferences(applicationContext).getToken().toString()
            }", followForm
        )
        call.enqueue(object : Callback<FollowModelCheck> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<FollowModelCheck>,
                response: Response<FollowModelCheck>
            ) {
                if (response.isSuccessful) {
                    checkFollow = response.body()?.result == true
                    if (checkFollow) {
                        binding.btnFollow.text = "Unfollow"
                    } else {
                        binding.btnFollow.text = "Follow"
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<FollowModelCheck>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Error, please try again later!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}