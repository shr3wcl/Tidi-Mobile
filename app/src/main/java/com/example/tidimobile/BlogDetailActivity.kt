package com.example.tidimobile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.ActivityBlogDetailBinding

class BlogDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogDetailBinding
    private lateinit var blogService: ApiBlogInterface
    private lateinit var idBlog: String
    private var url: String = "https://91ab-14-250-222-180.ngrok-free.app"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        blogService = ApiClient.getBlog()
        supportActionBar?.hide()
        title = intent.getStringExtra("title").toString()
        idBlog = intent.getStringExtra("id").toString()
        getDetailBlog()
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
}
