package com.example.tidimobile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.api.Url
import com.example.tidimobile.databinding.ActivityBlogDetailBinding
import com.example.tidimobile.databinding.ActivityInfoBlogBinding
import com.example.tidimobile.model.NotifyStoreModel
import com.example.tidimobile.model.ResponseMessage
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import okhttp3.Call
import org.w3c.dom.Comment
import retrofit2.Callback
import retrofit2.Response

class BlogDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogDetailBinding
    private lateinit var blogService: ApiBlogInterface
    private lateinit var idBlog: String
    private lateinit var idAuthor: String
    private lateinit var nameAuthor: String
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userPrefs: UserPreferences
    private lateinit var tokenPrefs: TokenPreferences


    private var url: String = Url().url
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        blogService = ApiClient.getBlog()
        userPrefs = UserPreferences(applicationContext)
        tokenPrefs = TokenPreferences(applicationContext)
//        supportActionBar?.hide()
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        title = intent.getStringExtra("title").toString()
        idBlog = intent.getStringExtra("id").toString()
        idAuthor = intent.getStringExtra("idUser").toString()
        nameAuthor = intent.getStringExtra("idUser").toString()

        if (idAuthor == userPrefs.getInfoUser()._id) {
            val intentT = Intent(applicationContext, EditBlogActivity::class.java)

            binding.navView.menu.findItem(R.id.item3).isEnabled = true
            binding.navView.menu.findItem(R.id.item4).isEnabled = true
            binding.navView.menu.findItem(R.id.item5).isEnabled = true

            binding.navView.menu.findItem(R.id.item1).title = "Like"
            binding.navView.menu.findItem(R.id.item2).title = "Comment"
            binding.navView.menu.findItem(R.id.item3).title = "Detail"
            binding.navView.menu.findItem(R.id.item4).title = "Edit"
            binding.navView.menu.findItem(R.id.item5).title = "Delete"
            binding.navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.item1 -> {
                        handleLikeBlog()
                    }
                    R.id.item2 -> {
                        val intentComment = Intent(applicationContext, CommentActivity::class.java)
                        intentComment.putExtra("idBlog", idBlog)
                        intentComment.putExtra("idAuthor", idAuthor)
                        startActivity(intentComment)
                    }
                    R.id.item3 -> {
                        val intentInfo = Intent(applicationContext, InfoBlogActivity::class.java)
                        intentInfo.putExtra("idBlog", idBlog)
                        startActivity(intentInfo)
                    }
                    R.id.item4 -> {
                        intentT.putExtra("id", idBlog)
                        intentT.putExtra("title", intent.getStringExtra("title").toString())
                        intentT.putExtra(
                            "description",
                            intent.getStringExtra("description").toString()
                        )
                        intentT.putExtra("idUser", idAuthor)
                        intentT.putExtra("nameAuthor", nameAuthor)
                        intentT.putExtra("status", intent.getStringExtra("status"))
                        startActivity(Intent(intentT))
                    }
                    R.id.item5 -> {
                        deleteBlog()
                    }
                }
                true
            }
        } else {
            val intentT = Intent(applicationContext, EditBlogActivity::class.java)
            binding.navView.menu.findItem(R.id.item1).title = "Like"
            binding.navView.menu.findItem(R.id.item2).title = "Comment"
            binding.navView.menu.findItem(R.id.item3).title = "Info"
            binding.navView.menu.findItem(R.id.item3).isEnabled = true
            binding.navView.menu.findItem(R.id.item4).title = ""
            binding.navView.menu.findItem(R.id.item4).isEnabled = false
            binding.navView.menu.findItem(R.id.item5).title = ""
            binding.navView.menu.findItem(R.id.item5).isEnabled = false
            binding.navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.item1 -> {
                        handleLikeBlog()
                    }
                    R.id.item2 -> {
                        val intentComment = Intent(applicationContext, CommentActivity::class.java)
                        intentComment.putExtra("idBlog", idBlog)
                        startActivity(intentComment)
                    }
                    R.id.item3 -> {
                        val intentInfo = Intent(applicationContext, InfoBlogActivity::class.java)
                        intentInfo.putExtra("idBlog", idBlog)
                        startActivity(intentInfo)
                    }
                }
                true
            }
        }

        getDetailBlog()
    }

    private fun deleteBlog() {
        val call = blogService.deleteBlog("Bearer ${tokenPrefs.getToken()}", idBlog)
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: retrofit2.Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                } else {
                    Toast.makeText(
                        applicationContext,
                        response.body().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Error, please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun handleLikeBlog() {
        blogService.increaseLikeBlog("Bearer ${tokenPrefs.getToken()}", idBlog)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: retrofit2.Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful) {
                        val message = response.body()?.message
                        val notify = NotifyStoreModel(
                            idAuthor,
                            "${userPrefs.getInfoUser().firstName} ${userPrefs.getInfoUser().lastName} liked your blog",
                            "Blog",
                            userPrefs.getInfoUser()._id,
                            idBlog
                        )
                        ApiClient.getNotify()
                            .storeNotify(idAuthor, "Bearer ${tokenPrefs.getToken()}", notify)
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
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error, please try again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ResponseMessage>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        "Error, please try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun getDetailBlog() {
        binding.wview.visibility = View.GONE
        binding.loadingBlog.visibility = View.VISIBLE
        val myWebView = binding.wview
        val webViewSetting = myWebView.settings
        webViewSetting.javaScriptEnabled = true
        webViewSetting.userAgentString = "User-agent"
        myWebView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith(url)) {
                    view.loadUrl(url)
                    return false
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            }
        }
        binding.loadingBlog.visibility = View.GONE
        binding.wview.visibility = View.VISIBLE
        myWebView.loadUrl("$url/get/${idBlog}")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
