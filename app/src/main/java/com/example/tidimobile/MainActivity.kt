package com.example.tidimobile

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.tidimobile.databinding.ActivityMainBinding
import com.example.tidimobile.fragment.UserFragment
import com.example.tidimobile.fragment.BlogFragment
import com.example.tidimobile.fragment.NotifyFragment
import com.example.tidimobile.fragment.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = "Blog"
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, BlogFragment.newInstance())
            addToBackStack(null)
            commit()
        }
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    title = "Blog"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container, BlogFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
                    true
                }
                R.id.navigation_search -> {
                    title = "Search"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container, SearchFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
                    true
                }
                R.id.navigation_notifications -> {
                    title = "Notify"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container, NotifyFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
                    true
                }
                R.id.navigation_user -> {
                    title = "User"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container, UserFragment.newInstance())
                        addToBackStack(null)
                        commit()
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}