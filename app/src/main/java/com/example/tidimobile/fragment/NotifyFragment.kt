package com.example.tidimobile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tidimobile.R
import com.example.tidimobile.adapter.NotifyAdapter
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.api.ApiNoteInterface
import com.example.tidimobile.api.ApiNotify
import com.example.tidimobile.databinding.FragmentNotifyBinding
import com.example.tidimobile.model.NotifyModel
import com.example.tidimobile.model.ResponseMessage
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotifyFragment : Fragment() {
    private lateinit var binding: FragmentNotifyBinding
    private lateinit var notifyApi: ApiNotify
    private lateinit var userPref: UserPreferences
    private lateinit var tokenPref: TokenPreferences
    private lateinit var listNotify: ArrayList<NotifyModel.SubNotifyModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navView: NavigationView = requireActivity().findViewById(R.id.navView)
        val menu: Menu = navView.menu

        menu.findItem(R.id.item1).title = "Hello"
        menu.findItem(R.id.item2).title = "Hello"
        menu.findItem(R.id.item1).title = "Hello"
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Notify"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifyBinding.inflate(layoutInflater)
        notifyApi = ApiClient.getNotify()
        userPref = UserPreferences(requireContext())
        tokenPref = TokenPreferences(requireContext())

        getNotify()
        return binding.root
    }

    private fun getNotify() {
        binding.rcViewListNotify.visibility = View.GONE
        binding.tvLoading.visibility = View.VISIBLE
        notifyApi.getNotify("Bearer ${tokenPref.getToken()}")
            .enqueue(object: Callback<NotifyModel>{
                override fun onResponse(
                    call: Call<NotifyModel>,
                    response: Response<NotifyModel>
                ) {
                    if(response.isSuccessful){
                        listNotify = response.body()?.notification!!
                        binding.rcViewListNotify.layoutManager = LinearLayoutManager(requireContext())
                        val nAdapter = NotifyAdapter(listNotify)
                        nAdapter.setOnClickItemListener(object: NotifyAdapter.OnClickNotifyListener{
                            override fun onClickNotify(position: Int) {
//                                val intent
                            }

                        })
                        binding.rcViewListNotify.adapter = nAdapter

                    }else{
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                    binding.rcViewListNotify.visibility = View.VISIBLE
                    binding.tvLoading.visibility = View.GONE
                }

                override fun onFailure(call: Call<NotifyModel>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }

            })
    }

    companion object {
        fun newInstance(): NotifyFragment = NotifyFragment()
    }
}