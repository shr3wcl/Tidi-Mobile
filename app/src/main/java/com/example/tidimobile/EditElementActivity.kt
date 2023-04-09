package com.example.tidimobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.tidimobile.databinding.ActivityEditElementBinding

class EditElementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditElementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditElementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Edit ${intent.getStringExtra("type")}"
        binding.editText.setText(intent.getStringExtra("data"))

        binding.btnClearText.setOnClickListener {
            binding.editText.setText("")
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                save()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun save() {
        val intentT = Intent(this, EditProfileActivity::class.java)
        intentT.putExtra("type", intent.getStringExtra("type"))
        intentT.putExtra("data", binding.editText.text.toString())
        startActivity(intentT)
    }
}