package com.example.tidimobile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tidimobile.R
import com.example.tidimobile.adapter.BlogsAdapter
import com.example.tidimobile.api.ApiBlogInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.FragmentBlogBinding
import com.example.tidimobile.databinding.FragmentUserBinding
import com.example.tidimobile.model.BlogModelBasic
import com.example.tidimobile.storage.TokenPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlogFragment : Fragment() {
    private lateinit var appPrefs: TokenPreferences
    private lateinit var binding: FragmentBlogBinding
    private lateinit var blogService: ApiBlogInterface
    private lateinit var listBlog: ArrayList<BlogModelBasic.BlogBasicObject>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlogBinding.inflate(layoutInflater, container, false)
        appPrefs = TokenPreferences(inflater.context)
        blogService = ApiClient.getBlog()
        getAllBlog()
        return binding.root
    }

    private fun getAllBlog() {
        binding.rcViewListBlog.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        val call = blogService.getAllBlogPublicUser()
        call.enqueue(object : Callback<BlogModelBasic> {
            override fun onResponse(
                call: Call<BlogModelBasic>,
                response: Response<BlogModelBasic>
            ) {
                if(response.isSuccessful){
                    listBlog = response.body()?.blogs!!
                    binding.rcViewListBlog.layoutManager = LinearLayoutManager(requireContext())
                    binding.rcViewListBlog.adapter = BlogsAdapter(listBlog)
                }
                binding.rcViewListBlog.visibility = View.VISIBLE
                binding.tvLoading.visibility = View.GONE
            }

            override fun onFailure(call: Call<BlogModelBasic>, t: Throwable) {
                Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT).show()
            }

        })
    }

    companion object {
        fun newInstance(): BlogFragment = BlogFragment()
    }
}