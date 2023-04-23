package com.example.tidimobile.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tidimobile.BlogDetailActivity
import com.example.tidimobile.adapter.BlogsAdapter
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.FragmentSearchBinding
import com.example.tidimobile.model.BlogModelBasic
import com.example.tidimobile.model.SearchKeyModel
import com.example.tidimobile.model.SearchModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private var searchSevice = ApiClient.getSearch()
    private lateinit var data: SearchModel
    private lateinit var listSearch: ArrayList<BlogModelBasic.BlogBasicObject>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Search"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, getTempData())
        binding.searchBar.setAdapter(adapter)
        binding.searchBtn.setOnClickListener {
            search()
        }
        return binding.root
    }

    private fun search() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken , 0)
        binding.rcViewSearch.visibility = View.GONE
//        binding.tvLoading.visibility = View.VISIBLE
        val keySearch = binding.searchBar.text.toString()
        searchSevice.search(SearchKeyModel(keySearch)).enqueue(object : Callback<BlogModelBasic>{
            override fun onResponse(
                call: Call<BlogModelBasic>,
                response: Response<BlogModelBasic>
            ) {
                if(response.isSuccessful){
                    listSearch = response.body()?.blogs!!
                    binding.rcViewSearch.layoutManager = LinearLayoutManager(requireContext())
                    val bAdapter = BlogsAdapter(listSearch)
                    bAdapter.setOnItemClickListener(object : BlogsAdapter.OnBlogClickListener {
                        override fun onClickBlog(position: Int) {
                            val intentS = Intent(context, BlogDetailActivity::class.java)
                            intentS.putExtra("id", listSearch[position]._id)
                            intentS.putExtra("title", listSearch[position].title)
                            intentS.putExtra("description", listSearch[position].description)
                            intentS.putExtra(
                                "author",
                                "${listSearch[position].idUser?.firstName} ${listSearch[position].idUser?.lastName}"
                            )
                            intentS.putExtra("idUser", listSearch[position].idUser?._id)
                            intentS.putExtra("status", listSearch[position].status)
                            startActivity(intentS)
                        }
                    })
                    binding.rcViewSearch.adapter = bAdapter
                    binding.rcViewSearch.visibility = View.VISIBLE
                }else{
                    Toast.makeText(context, "Error 1", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BlogModelBasic>, t: Throwable) {
                Toast.makeText(context, "Error 2", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getTempData(): ArrayList<Any> {
        val suggestion = ArrayList<Any>()
        searchSevice.get4Search().enqueue(object: Callback<SearchModel>{
            override fun onResponse(call: Call<SearchModel>, response: Response<SearchModel>) {
                if(response.isSuccessful){
                    data = response.body()!!
                    val users = response.body()!!.data?.users
                    val blogs = response.body()!!.data?.blogs
                    val listUsers = users?.map { it.firstName + " " + it.lastName }!!.toTypedArray()
                    val listBlogs = blogs?.map { it.title.toString() }!!.toTypedArray()
                    suggestion.addAll(listBlogs)
                    suggestion.addAll(listUsers)
                }
                else{
                    data = SearchModel()
                }
            }
            override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                data = SearchModel()
            }
        })
        return suggestion
    }

    companion object {
        fun newInstance() = SearchFragment().apply {}
    }
}