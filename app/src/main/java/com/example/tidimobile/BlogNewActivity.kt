package com.example.tidimobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tidimobile.databinding.ActivityBlogNewBinding

class BlogNewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlogNewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}