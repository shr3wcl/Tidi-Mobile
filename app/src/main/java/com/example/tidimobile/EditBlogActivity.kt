package com.example.tidimobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.ActivityEditBlogBinding
import com.example.tidimobile.dialog.BlogDialog
import com.example.tidimobile.model.BlogModel
import com.example.tidimobile.model.ContentObject
import com.example.tidimobile.model.ResponseMessage
import com.example.tidimobile.storage.TokenPreferences
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var dataBlogSave: ContentObject

class EditBlogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBlogBinding
    private var url: String = "https://278b-14-250-222-180.ngrok-free.app"
    private lateinit var idBlog: String
    private lateinit var titleBlog: String
    private lateinit var desBlog: String
    private lateinit var idUser: String
    private lateinit var tokenPrefs: TokenPreferences
    private lateinit var blogService: ApiBlogInterface
    private var statusBlog = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBlogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        blogService = ApiClient.getBlog()

        idBlog = intent.getStringExtra("id").toString()
        titleBlog = intent.getStringExtra("title").toString()
        desBlog = intent.getStringExtra("description").toString()
        idUser = intent.getStringExtra("idUser").toString()
        statusBlog = when(intent.getStringExtra("status")){
            "true" -> true
            "false" -> false
            else -> true
        }
        tokenPrefs = TokenPreferences(applicationContext)
        title = "Edit blog"
        getDetailBlogToEdit()
        check()
    }

    private fun check() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val dialog = BlogDialog(this, object : BlogDialog.BlogDialogListener{
                    override fun onBlogSelected(title: String, description: String, dataBlog: ContentObject, statBlog: Boolean) {
                        titleBlog = title
                        desBlog = description
                        dataBlogSave = dataBlog
                        statusBlog = statBlog
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

    private fun saveBlog(){
        val blogEditedData = BlogModel.BlogObject(idBlog, idUser, titleBlog, statusBlog, null, dataBlogSave, desBlog)
        val call = blogService.editBlog("Bearer ${tokenPrefs.getToken()}", blogEditedData, idBlog)
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if(response.isSuccessful){
                    Toast.makeText(applicationContext, response.body()?.message.toString(), Toast.LENGTH_LONG).show()
                    val intentM = Intent(applicationContext, BlogDetailActivity::class.java)
                    intentM.putExtra("id", idBlog)
                    intentM.putExtra("title", titleBlog)
                    intentM.putExtra("description", desBlog)
                    intentM.putExtra("author", intent.getStringExtra("nameAuthor").toString())
                    intentM.putExtra("idUser", idUser)
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
        val webAppInterface = WebAppInterface(this)
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

        myWebView.loadUrl("$url/get/${idBlog}/edit", headers)
    }
}

class WebAppInterface(val context: Context) {
    private var dataChangeValue: ContentObject? = null

    @JavascriptInterface
    fun onDataChanged(dataChange: String) {
        val gson = Gson()
        val topic = gson.fromJson(dataChange, ContentObject::class.java)
        dataChangeValue = topic
        dataBlogSave = topic
    }
}