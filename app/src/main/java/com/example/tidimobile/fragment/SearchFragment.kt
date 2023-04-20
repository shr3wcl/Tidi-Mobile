package com.example.tidimobile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.databinding.FragmentSearchBinding
import com.example.tidimobile.model.SearchModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private var searchSevice = ApiClient.getSearch()
    private lateinit var data: SearchModel
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