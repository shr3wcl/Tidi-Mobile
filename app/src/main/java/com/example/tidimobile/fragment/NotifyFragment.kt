package com.example.tidimobile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.example.tidimobile.R
import com.example.tidimobile.databinding.FragmentNotifyBinding
import com.google.android.material.navigation.NavigationView

class NotifyFragment : Fragment() {
    private lateinit var binding: FragmentNotifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navView: NavigationView = requireActivity().findViewById(R.id.navView)
        val menu: Menu = navView.menu

        menu.findItem(R.id.item1).title = "Hello"
        menu.findItem(R.id.item2).title = "Hello"
        menu.findItem(R.id.item1).title = "Hello"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifyBinding.inflate(layoutInflater)

        getNotify()
        return binding.root
    }

    private fun getNotify() {

    }

    companion object {
        fun newInstance(): NotifyFragment = NotifyFragment()
    }
}