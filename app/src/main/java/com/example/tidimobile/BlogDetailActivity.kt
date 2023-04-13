package com.example.tidimobile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.ActivityBlogDetailBinding
import com.example.tidimobile.fragment.MyBlogFragment
import com.example.tidimobile.storage.UserPreferences
import com.google.android.material.navigation.NavigationView

class BlogDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogDetailBinding
    private lateinit var blogService: ApiBlogInterface
    private lateinit var idBlog: String
    private lateinit var idAuthor: String
    private lateinit var nameAuthor: String
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var userPrefs: UserPreferences


    private var url: String = "https://7e7a-14-250-222-180.ngrok-free.app"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        blogService = ApiClient.getBlog()
        userPrefs = UserPreferences(applicationContext)

//        supportActionBar?.hide()
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        title = intent.getStringExtra("title").toString()
        idBlog = intent.getStringExtra("id").toString()
        idAuthor = intent.getStringExtra("idUser").toString()
        nameAuthor = intent.getStringExtra("idUser").toString()

        if(idAuthor == userPrefs.getInfoUser()._id){
            val intentT = Intent(applicationContext, EditBlogActivity::class.java)

            binding.navView.menu.findItem(R.id.item1).title = "Edit"
            binding.navView.menu.findItem(R.id.item2).title = "Like"
            binding.navView.menu.findItem(R.id.item3).title = "Comment"
            binding.navView.menu.findItem(R.id.item4).title = "Delete"
            binding.navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.item1 -> {
                        intentT.putExtra("id", idBlog)
                        intentT.putExtra("title", intent.getStringExtra("title").toString())
                        intentT.putExtra("description", intent.getStringExtra("description").toString())
                        intentT.putExtra("idUser", idAuthor)
                        intentT.putExtra("nameAuthor", nameAuthor)
                        startActivity(Intent(intentT))
                    }
                    R.id.item2 -> Toast.makeText(applicationContext, "Clicked 2", Toast.LENGTH_SHORT).show()
                    R.id.item3 -> Toast.makeText(applicationContext, "Clicked 3", Toast.LENGTH_SHORT).show()
                    R.id.item4 -> Toast.makeText(applicationContext, "Clicked 4", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }else{
            binding.navView.menu.findItem(R.id.item1).title = "Like"
            binding.navView.menu.findItem(R.id.item2).title = "Comment"
            binding.navView.menu.findItem(R.id.item3).title = ""
            binding.navView.menu.findItem(R.id.item3).isEnabled = false
            binding.navView.menu.findItem(R.id.item4).title = ""
            binding.navView.menu.findItem(R.id.item4).isEnabled = false
        }

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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
