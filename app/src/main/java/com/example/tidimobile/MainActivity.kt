/**
 * Phạm Minh Trí VKU
 */
package com.example.tidimobile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.tidimobile.databinding.ActivityMainBinding
import com.example.tidimobile.fragment.UserFragment
import com.example.tidimobile.fragment.BlogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item1 -> Toast.makeText(applicationContext, "Clicked 1", Toast.LENGTH_SHORT).show()
                R.id.item2 -> Toast.makeText(applicationContext, "Clicked 2", Toast.LENGTH_SHORT).show()
                R.id.item3 -> Toast.makeText(applicationContext, "Clicked 3", Toast.LENGTH_SHORT).show()
                R.id.item4 -> Toast.makeText(applicationContext, "Clicked 4", Toast.LENGTH_SHORT).show()
            }
            true
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
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
                R.id.navigation_dashboard -> {
                    title = "Dashboard"
                    true
                }
                R.id.navigation_notifications -> {
                    title = "Notification"
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