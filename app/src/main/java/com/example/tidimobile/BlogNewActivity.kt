package com.example.tidimobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.api.Url
import com.example.tidimobile.databinding.ActivityBlogNewBinding
import com.example.tidimobile.dialog.BlogDialog
import com.example.tidimobile.model.BlogNewModel
import com.example.tidimobile.model.ContentObject
import com.example.tidimobile.model.ResponseMessage
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var dataBlogSave: ContentObject

class BlogNewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogNewBinding
    private lateinit var tokenPrefs: TokenPreferences
    private var url: String = Url().url
    private lateinit var titleBlog: String
    private lateinit var desBlog: String
    private var statusBlog = true
    private lateinit var blogService: ApiBlogInterface
    private lateinit var userPrefs: UserPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        titleBlog = ""
        desBlog = ""
        dataBlogSave = ContentObject()
        tokenPrefs = TokenPreferences(applicationContext)
        blogService = ApiClient.getBlog()
        userPrefs = UserPreferences(applicationContext)
        title = "New Blog (Touch the screen)"
        getDetailBlogToEdit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val dialog = BlogDialog(this, object : BlogDialog.BlogDialogListener{
                    override fun onBlogSelected(title: String, description: String, dataBlog: ContentObject, statusBg: Boolean) {
                        titleBlog = title
                        desBlog = description
                        dataBlogSave = dataBlog
                        statusBlog = statusBg
                        saveBlog()
                    }
                }, titleBlog, desBlog, dataBlogSave, statusBlog)
                dialog.setCancelable(true)
                dialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveBlog() {
        val blogEditedData = BlogNewModel(userPrefs.getInfoUser()._id, titleBlog, dataBlogSave, desBlog, statusBlog)
        val call = blogService.saveNewBlog("Bearer ${tokenPrefs.getToken()}", blogEditedData)
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if(response.isSuccessful){
                    Toast.makeText(applicationContext, response.body()?.message.toString(), Toast.LENGTH_LONG).show()
                    val intentM = Intent(applicationContext, MainActivity::class.java)
//                    intentM.putExtra("id", idBlog)
//                    intentM.putExtra("title", titleBlog)
//                    intentM.putExtra("description", desBlog)
//                    intentM.putExtra("author", intent.getStringExtra("nameAuthor").toString())
//                    intentM.putExtra("idUser", idUser)
                    startActivity(intentM)
                }
                else{
                    Toast.makeText(applicationContext, "Error 403", Toast.LENGTH_LONG).show()

                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()

            }

        })
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun getDetailBlogToEdit() {
        val webAppInterface = WebAppNewInterface(this)
        binding.wview.visibility = View.GONE
        binding.loadingBlog.visibility = View.VISIBLE
        val myWebView = binding.wview
        val webViewSetting = myWebView.settings
        webViewSetting.javaScriptEnabled = true
        webViewSetting.userAgentString = "User-agent"
        myWebView.addJavascriptInterface(webAppInterface, "Android")

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
        val headers: HashMap<String, String> = HashMap()
        headers["token"] = "Bearer ${tokenPrefs.getToken().toString()}"

        myWebView.loadUrl("$url/get/new", headers)
    }
}

class WebAppNewInterface(val context: Context) {
    private var dataChangeValue: ContentObject? = null

    @JavascriptInterface
    fun onDataChanged(dataChange: String) {
        val gson = Gson()
        val topic = gson.fromJson(dataChange, ContentObject::class.java)
        dataChangeValue = topic
        dataBlogSave = topic
    }
}