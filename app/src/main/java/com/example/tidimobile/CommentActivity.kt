package com.example.tidimobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tidimobile.adapter.CommentAdapter
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.ActivityCommentBinding
import com.example.tidimobile.model.CommentModel
import com.example.tidimobile.model.CommentNewModel
import com.example.tidimobile.model.NotifyStoreModel
import com.example.tidimobile.model.ResponseMessage
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private lateinit var blogService: ApiBlogInterface
    private lateinit var idBlog: String
    private lateinit var idAuthor: String
    private lateinit var listComment: ArrayList<CommentModel>
    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var userPref: UserPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Comment"
        tokenPreferences = TokenPreferences(applicationContext)
        userPref = UserPreferences(applicationContext)
        blogService = ApiClient.getBlog()
        idBlog = intent.getStringExtra("idBlog").toString()
        idAuthor = intent.getStringExtra("idAuthor").toString()
        getComments()

        binding.btnSendCmt.setOnClickListener {
            saveComment()
        }
    }

    private fun saveComment() {

        val content = CommentNewModel(binding.edTextCmt.text.toString())
        blogService.addNewComment("bearer ${tokenPreferences.getToken()}", content, idBlog)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful) {
                        binding.edTextCmt.setText("")
                        val notify = NotifyStoreModel(
                            idAuthor,
                            "${userPref.getInfoUser().firstName} ${userPref.getInfoUser().lastName} commented on your post",
                            "Comment",
                            userPref.getInfoUser()._id,
                            idBlog
                        )
                        ApiClient.getNotify()
                            .storeNotify(idAuthor, "Bearer ${tokenPreferences.getToken()}", notify)
                            .enqueue(object : Callback<ResponseMessage> {
                                override fun onResponse(
                                    call: retrofit2.Call<ResponseMessage>,
                                    response: Response<ResponseMessage>
                                ) {
                                }

                                override fun onFailure(
                                    call: retrofit2.Call<ResponseMessage>,
                                    t: Throwable
                                ) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Error 2",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            })
                        Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()

                        getComments()
                    } else {
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun getComments() {
        binding.rcViewListComment.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        blogService.getComments(idBlog).enqueue(object : Callback<ArrayList<CommentModel>> {
            override fun onResponse(
                call: Call<ArrayList<CommentModel>>,
                response: Response<ArrayList<CommentModel>>
            ) {
                if (response.isSuccessful) {
                    listComment = response.body()!!
                    binding.rcViewListComment.layoutManager =
                        LinearLayoutManager(applicationContext)
                    val adapter = CommentAdapter(listComment)
                    adapter.setOnItemClickListener(object : CommentAdapter.OnCmtClickListener {
                        override fun onClickCmt(position: Int) {
                            val intentT = Intent(applicationContext, InfoUserActivity::class.java)
                            intentT.putExtra("name", listComment[position].idUser?.firstName + " " + listComment[position].idUser?.lastName)
                            intentT.putExtra("idUserBlog", listComment[position].idUser?._id)
                            startActivity(intentT)
                        }
                    })
                    binding.rcViewListComment.adapter = adapter
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
                binding.rcViewListComment.visibility = View.VISIBLE
                binding.tvLoading.visibility = View.GONE
            }

            override fun onFailure(call: Call<ArrayList<CommentModel>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }

        })
    }
}