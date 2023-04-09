package com.example.tidimobile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.api.ApiUserInterface
import com.example.tidimobile.databinding.ActivityEditElementBinding
import com.example.tidimobile.databinding.ActivityEditProfileBinding
import com.example.tidimobile.model.UserChangedModel
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var userPrefs: UserPreferences
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userService: ApiUserInterface
    private lateinit var tokenPrefs: TokenPreferences

    @SuppressLint("PrivateResource", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPrefs = UserPreferences(applicationContext)
        userService = ApiClient.getUser()
        tokenPrefs = TokenPreferences(applicationContext)
        title = "Edit Profile"
        getInfo()
        binding.txtBirthdayProfile.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->

                    val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year >"
                    binding.txtBirthdayProfile.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        if(intent.getStringExtra("type") != null){
            val data = intent.getStringExtra("data")
            when(intent.getStringExtra("type")){
                "First Name" -> binding.txtFirstNameProfile.text = "$data >"
                "Last Name" -> binding.txtLastNameProfile.text = "$data >"
                "Email" -> binding.txtEmail.text = "$data >"
                "Bio" -> binding.txtBioProfile.text = "$data >"
            }
        }
        val intentT = Intent(this, EditElementActivity::class.java)
        binding.txtFirstNameProfile.setOnClickListener {
            intentT.putExtra("type", "First Name")
            intentT.putExtra("data", binding.txtFirstNameProfile.text.toString().trimEnd('>').trim())
            startActivity(intentT)
        }
        binding.txtLastNameProfile.setOnClickListener {
            intentT.putExtra("type", "Last Name")
            intentT.putExtra("data", binding.txtLastNameProfile.text.toString().trimEnd('>').trim())
            startActivity(intentT)
        }
        binding.txtEmail.setOnClickListener {
            intentT.putExtra("type", "Email")
            intentT.putExtra("data", binding.txtEmail.text.toString().trimEnd('>').trim())
            startActivity(intentT)
        }
        binding.txtBioProfile.setOnClickListener {
            intentT.putExtra("type", "Bio")
            intentT.putExtra("data", binding.txtBioProfile.text.toString().trimEnd('>').trim())
            startActivity(intentT)
        }
        binding.txtGenderProfile.setOnClickListener {
            val dialog = GenderDialog(this, object : GenderDialog.GenderDialogListener{
                override fun onGenderSelected(gender: String) {
                    binding.txtGenderProfile.text = gender
                }
            })
            dialog.setCancelable(true)
            dialog.show()
        }
        binding.txtPassword.setOnClickListener {

        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                saveInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveInfo() {
        val firstName = binding.txtFirstNameProfile.text.toString().trimEnd('>').trim()
        val lastName = binding.txtLastNameProfile.text.toString().trimEnd('>').trim()
        val birthday = binding.txtBirthdayProfile.text.toString().trimEnd('>').trim()
        val bio = binding.txtBioProfile.text.toString().trimEnd('>').trim()
        val email = binding.txtEmail.text.toString().trimEnd('>').trim()
        val gender = binding.txtGenderProfile.text.toString().trimEnd('>').trim()

        val user = UserChangedModel(firstName, lastName, email, gender, birthday, bio)
        val call = userService.changeInfoUser(userPrefs.getInfoUser()._id.toString(), "Bearer ${tokenPrefs.getToken()}", user)
        call.enqueue(object : Callback<UserChangedModel> {
            override fun onResponse(
                call: Call<UserChangedModel>,
                response: Response<UserChangedModel>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext, "An error occurred, please try again later!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserChangedModel>, t: Throwable) {
                Toast.makeText(applicationContext, "Failure", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun getInfo() {
        val user = userPrefs.getInfoUser()

        val rawDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val date: Date = user.createdAt?.let { rawDateFormat.parse(it) } as Date
        val outputFormat = SimpleDateFormat("HH:mm:ss - dd/MM/yyyy")
        val dateFormat = outputFormat.format(date)
        binding.imageViewAvatar.setImageBitmap(user.avatar?.let { base64ToBitmap(it) })
        binding.txtGenderProfile.text = user.gender + " >"
        binding.txtUsernameProfile.text = user.username
        binding.txtFirstNameProfile.text = user.firstName + " >"
        binding.txtLastNameProfile.text = user.lastName + " >"
        binding.txtBirthdayProfile.text = user.birthday + " >"
        binding.txtEmail.text = user.email + " >"
//        binding.txtBioProfile.text = user.bio  ?.substring(0, 20) + "... >"
        binding.txtTimeCreated.text = dateFormat
    }



    private fun base64ToBitmap(base64String: String): Bitmap? {
        val decodedString = Base64.decode(base64String.split(",")[1], Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }


}