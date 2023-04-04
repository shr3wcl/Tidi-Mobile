package com.example.tidimobile.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tidimobile.R

import com.example.tidimobile.databinding.FragmentUserBinding
import com.example.tidimobile.storage.TokenPreferences


class UserFragment : Fragment() {
    private lateinit var appPrefs: TokenPreferences

    private lateinit var binding: FragmentUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        appPrefs = TokenPreferences(inflater.context)

        if (appPrefs.getToken()?.isEmpty() == false) {
            childFragmentManager.beginTransaction()
                .add(R.id.fragment_container_user, UserInfoFragment.newInstance()).commit()
        } else {
            childFragmentManager.beginTransaction()
                .add(R.id.fragment_container_user, LoginFragment.newInstance()).commit()
        }
        return binding.root
    }

    companion object {
        fun newInstance(): UserFragment = UserFragment()
    }


}