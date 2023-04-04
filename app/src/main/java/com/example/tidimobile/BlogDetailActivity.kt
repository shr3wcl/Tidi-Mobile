package com.example.tidimobile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.ActivityBlogDetailBinding
import com.example.tidimobile.model.BlogModel
import com.example.tidimobile.model.BlogModelDetail
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import work.upstarts.editorjskit.environment.dp
import work.upstarts.editorjskit.models.EJBlock
import work.upstarts.editorjskit.models.HeadingLevel
import work.upstarts.editorjskit.ui.EditorJsAdapter
import work.upstarts.editorjskit.ui.theme.EJStyle
import work.upstarts.gsonparser.EJDeserializer

class BlogDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogDetailBinding
    private lateinit var blogService: ApiBlogInterface
    private lateinit var idBlog: String
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
    private fun getDetailBlog(){
        val myWebView = binding.wview
        val webviewSetting = myWebView.settings
        webviewSetting.javaScriptEnabled = true
        webviewSetting.userAgentString = "User-agent"
        myWebView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("https://fa24-14-250-222-180.ap.ngrok.io")) {
                    view.loadUrl(url)
                    return false
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            }
        }
        myWebView.loadUrl("https://fa24-14-250-222-180.ap.ngrok.io/get/${idBlog}")


//        webview.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView?, url: String?) {
//                // Tải xong trang, thực thi mã JavaScript
//                webview.evaluateJavascript(
//                    """
//            const currentUrl = window.location.href;
//            const baseUrl = 'https://454e-14-250-222-180.ap.ngrok.io';
//            history.pushState({}, null, baseUrl+'/get/'+id);
//            """.trimIndent(),
//                    null
//                )
//            }
//        }
    }
//    private fun getDetailBlog() {
//        val call = blogService.getDetailBlog(idBlog)
//        call.enqueue(object : Callback<BlogModelDetail> {
//            override fun onResponse(
//                call: Call<BlogModelDetail>,
//                response: Response<BlogModelDetail>
//            ) {
//                if (response.isSuccessful) {
//                    GlobalScope.launch(Dispatchers.Main) {
//                        with(binding.recyclerView) {
//                            layoutManager = LinearLayoutManager(context)
//                            adapter = rvAdapter
//                        }
//                        val blocksType = object : TypeToken<MutableList<EJBlock>>() {}.type
//                        val gson = GsonBuilder()
//                            .registerTypeAdapter(blocksType, EJDeserializer())
//                            .create()
//                        val dummyData = gson.toJson(response.body()?.blog?.content)
//                        runOnUiThread {
//                            val ejResponse = gson.fromJson(dummyData, EJResponse::class.java)
//                            rvAdapter.items = ejResponse.blocks
//                            Toast.makeText(this@BlogDetailActivity, "Hello", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<BlogModelDetail>, t: Throwable) {
//                Toast.makeText(this@BlogDetailActivity, "Error", Toast.LENGTH_SHORT).show()
//
//            }
//
//        })
//    }

    private val rvAdapter: EditorJsAdapter by lazy {
        EditorJsAdapter(this?.let { ContextCompat.getColor(it, R.color.black) }?.let {
            EJStyle.builderWithDefaults(this)
                .linkColor(it)
                .linkColor(ContextCompat.getColor(this, R.color.black))
                .headingMargin(
                    ZERO_MARGIN,
                    ZERO_MARGIN,
                    ZERO_MARGIN,
                    ZERO_MARGIN,
                    HeadingLevel.h1
                )
                .headingMargin(
                    STANDARD_MARGIN,
                    ZERO_MARGIN,
                    ZERO_MARGIN,
                    ZERO_MARGIN,
                    HeadingLevel.h2
                )
                .linkColor(ContextCompat.getColor(this, R.color.black))
                .dividerBreakHeight(DIVIDER_HEIGHT.dp)
                .dividerMargin(0.dp, 4.dp, 0.dp, 4.dp)
                .dividerBreakColor(Color.parseColor("#32000000"))
                .build()
        })
    }
    fun readFileFromAssets(fname: String, assetsManager: AssetManager) =
        assetsManager.open(fname).readBytes().toString(Charsets.UTF_8)

    val ZERO_MARGIN = 0
    val STANDARD_MARGIN = 16
    val DIVIDER_HEIGHT = 1
}

data class EJResponse(val blocks: List<EJBlock>)
