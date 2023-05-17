package com.example.tidimobile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tidimobile.adapter.UserOVAdapter
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.ActivityInfoBlogBinding
import com.example.tidimobile.model.BlogOverviewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InfoBlogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBlogBinding
    private lateinit var blogService: ApiBlogInterface
    private lateinit var idBlog: String
    private lateinit var listLiked: ArrayList<BlogOverviewModel.LikeOVModel>
    private lateinit var nameAuthor: String
    private lateinit var idUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idBlog = intent.getStringExtra("idBlog").toString()
        blogService = ApiClient.getBlog()
        title = "Detail Blog"
        getData()
        binding.authorBtn.setOnClickListener {
            val intentT = Intent(applicationContext, InfoUserActivity::class.java)
            intentT.putExtra("name", nameAuthor)
            intentT.putExtra("idUserBlog", idUser)
            startActivity(intentT)
        }
    }

    private fun getData() {
        blogService.overViewBlog(idBlog)
            .enqueue(object : Callback<BlogOverviewModel> {
                @SuppressLint("SetTextI18n", "SimpleDateFormat")
                override fun onResponse(
                    call: Call<BlogOverviewModel>,
                    response: Response<BlogOverviewModel>
                ) {
                    if (response.isSuccessful) {
                        binding.txtTitleOV.text = response.body()?.blog?.title!!
                        binding.txtDesOV.text = response.body()?.blog?.description!!
                        nameAuthor =
                            "${response.body()?.blog?.idUser?.firstName} ${response.body()?.blog?.idUser?.lastName}"
                        idUser = response.body()?.blog?.idUser?._id.toString()
                        binding.tvAuthorName.text =
                            "${response.body()?.blog?.idUser?.firstName} ${response.body()?.blog?.idUser?.lastName}"

                        Glide.with(applicationContext)
                            .load(base64ToBitmap(response.body()?.blog?.idUser?.avatar!!))
                            .circleCrop().diskCacheStrategy(
                                DiskCacheStrategy.ALL
                            ).into(binding.imgOVAuthor)
                        binding.imgOVAuthor.setImageBitmap(base64ToBitmap(response.body()?.blog?.idUser?.avatar!!))
                        val rawDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                        val date: Date =
                            response.body()?.blog?.createdAt!!.let { rawDateFormat.parse(it) } as Date
//                        val outputFormat = SimpleDateFormat("HH:mm:ss - dd/MM/yyyy")
                        val outputFormat = SimpleDateFormat("dd/MM/yyyy")
                        val dateFormat = outputFormat.format(date)
                        binding.tvDateInfo.text = dateFormat
                        listLiked = response.body()?.like!!

                        binding.viewTotalTxt.text = listLiked.size.toString()

                        binding.rcViewListUserLike.layoutManager =
                            LinearLayoutManager(applicationContext)
                        val lAdapter = UserOVAdapter(listLiked)
                        lAdapter.setOnItemClickListener(object :
                            UserOVAdapter.OnUserOVClickListener {
                            override fun onClickUser(position: Int) {
                                val intentUser =
                                    Intent(applicationContext, InfoUserActivity::class.java)
                                intentUser.putExtra("idUserBlog", listLiked[position].idUser?._id)
                                intentUser.putExtra(
                                    "name",
                                    listLiked[position].idUser?.firstName + " " + listLiked[position].idUser?.lastName
                                )
                                startActivity(intentUser)
                            }

                        })
                        binding.rcViewListUserLike.adapter = lAdapter
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error, please try again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BlogOverviewModel>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "Error, please try again!",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })

    }

    private fun base64ToBitmap(base64String: String): Bitmap? {
//        val decodedString = Base64.decode(base64String.split(",")[1], Base64.DEFAULT)
//        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}