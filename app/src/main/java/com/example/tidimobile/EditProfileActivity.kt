package com.example.tidimobile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tidimobile.api.ApiAuthInterface
import com.example.tidimobile.api.ApiClient
import com.example.tidimobile.api.ApiUserInterface
import com.example.tidimobile.databinding.ActivityEditProfileBinding
import com.example.tidimobile.dialog.ChangePasswordDialog
import com.example.tidimobile.dialog.GenderDialog
import com.example.tidimobile.model.*
import com.example.tidimobile.storage.TokenPreferences
import com.example.tidimobile.storage.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var userPrefs: UserPreferences
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userService: ApiUserInterface
    private lateinit var tokenPrefs: TokenPreferences
    private lateinit var service: ApiAuthInterface
    private val pickImageRequest = 1
    private var filePath: Uri? = null

    @SuppressLint("PrivateResource", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPrefs = UserPreferences(applicationContext)
        userService = ApiClient.getUser()
        tokenPrefs = TokenPreferences(applicationContext)
        service = ApiClient.getService()

        title = "Edit Profile"
        getInfo()
        binding.btnChangeAvatar.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityIfNeeded(intent, pickImageRequest)
        }
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
        if (intent.getStringExtra("type") != null) {
            val data = intent.getStringExtra("data")
            when (intent.getStringExtra("type")) {
                "First Name" -> binding.txtFirstNameProfile.text = "$data >"
                "Last Name" -> binding.txtLastNameProfile.text = "$data >"
                "Email" -> binding.txtEmail.text = "$data >"
                "Bio" -> binding.txtBioProfile.text = "$data >"
            }
        }
        val intentT = Intent(this, EditElementActivity::class.java)
        binding.txtFirstNameProfile.setOnClickListener {
            intentT.putExtra("type", "First Name")
            intentT.putExtra(
                "data",
                binding.txtFirstNameProfile.text.toString().trimEnd('>').trim()
            )
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
            val dialog = GenderDialog(this, object : GenderDialog.GenderDialogListener {
                override fun onGenderSelected(gender: String) {
                    binding.txtGenderProfile.text = gender
                }
            })
            dialog.setCancelable(true)
            dialog.show()
        }
        binding.txtPassword.setOnClickListener {
            val dialog =
                ChangePasswordDialog(this, object : ChangePasswordDialog.PasswordDialogListener {
                    override fun onChangePasswordSelected(
                        currentPassword: String,
                        newPassword: String,
                        confirmNewPassword: String
                    ) {
                        changePassword(currentPassword, newPassword, confirmNewPassword)
                    }
                })
            dialog.setCancelable(true)
            dialog.show()
        }

    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageRequest && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val inputStream = contentResolver.openInputStream(filePath!!)
                val bytes = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                    bytes.write(buffer, 0, bytesRead)
                }
                val avatar = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT)


                val call =
                    userService.changeAvatar("Bearer ${tokenPrefs.getToken()}", AvatarModel(avatar))
                call.enqueue(object : Callback<ResponseMessage> {
                    override fun onResponse(
                        call: Call<ResponseMessage>,
                        response: Response<ResponseMessage>
                    ) {
                        if (response.isSuccessful) {
                            userPrefs.changeAvatar(avatar)
                            Glide.with(applicationContext).load(base64ToBitmap(avatar)).circleCrop()
                                .diskCacheStrategy(
                                    DiskCacheStrategy.ALL
                                ).into(binding.imageViewAvatar)
                            Toast.makeText(
                                applicationContext,
                                "Change Avatar Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                    }

                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
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
        val firstName =
            capitalizeWords(binding.txtFirstNameProfile.text.toString().trimEnd('>').trim())
        val lastName =
            capitalizeWords(binding.txtLastNameProfile.text.toString().trimEnd('>').trim())
        val birthday = binding.txtBirthdayProfile.text.toString().trimEnd('>').trim()
        val bio = binding.txtBioProfile.text.toString().trimEnd('>').trim()
        val email = binding.txtEmail.text.toString().trimEnd('>').trim()
        val gender = binding.txtGenderProfile.text.toString().trimEnd('>').trim()

        val user = UserChangedModel(firstName, lastName, email, gender, birthday, bio)
        val call = userService.changeInfoUser(
            userPrefs.getInfoUser()._id.toString(),
            "Bearer ${tokenPrefs.getToken()}",
            user
        )
        call.enqueue(object : Callback<UserEditResponse> {
            override fun onResponse(
                call: Call<UserEditResponse>,
                response: Response<UserEditResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.user?.let { userPrefs.saveInfoEditor(it) }
                    Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                } else {
                    Toast.makeText(
                        applicationContext,
                        "An error occurred, please try again later!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserEditResponse>, t: Throwable) {
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
        try {
            Glide.with(this).load(user.avatar?.let { base64ToBitmap(it) }).circleCrop()
                .diskCacheStrategy(
                    DiskCacheStrategy.ALL
                ).into(binding.imageViewAvatar)
            binding.imageViewAvatar.setImageBitmap(user.avatar?.let { base64ToBitmap(it) })
        } catch (e: java.lang.Exception) {
            Toast.makeText(applicationContext, "Cannot load image now", Toast.LENGTH_SHORT).show()
        }
        binding.txtGenderProfile.text = user.gender + " >"
        binding.txtUsernameProfile.text = user.username
        binding.txtFirstNameProfile.text = user.firstName + " >"
        binding.txtLastNameProfile.text = user.lastName + " >"
        binding.txtBirthdayProfile.text = user.birthday + " >"
        binding.txtEmail.text = user.email + " >"
//        binding.txtBioProfile.text = user.bio  ?.substring(0, 20) + "... >"
        binding.txtTimeCreated.text = dateFormat
    }

    private fun changePassword(currentPassword: String, newPwd: String, retypePwd: String) {
        if (newPwd != retypePwd) {
            Toast.makeText(applicationContext, "Password not match", Toast.LENGTH_SHORT).show()
//            findViewById<EditText>(R.id.txtNewPassword).error = "Password not match"
            return
        }
        val newPasswd = UserChangePwdModel(currentPassword, newPwd)
        val call = userService.changePassword("Bearer ${tokenPrefs.getToken()}", newPasswd)
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "${response.body()?.message}, please login again!",
                        Toast.LENGTH_SHORT
                    ).show()
                    logout()
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()

            }

        })
    }

    private fun base64ToBitmap(base64String: String): Bitmap? {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun logout() {
        val call = service.logoutUser("Bearer ${tokenPrefs.getToken()}")
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(
                call: Call<ResponseMessage>,
                response: Response<ResponseMessage>
            ) {
                Toast.makeText(
                    applicationContext,
                    response.body()?.message.toString(),
                    Toast.LENGTH_SHORT
                )
                    .show()
                tokenPrefs.clearToken()
                userPrefs.clearInfo()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun capitalizeWords(str: String): String {
        val words = str.split(" ")
        val capitalizedWords = mutableListOf<String>()
        for (word in words) {
            if (word.isNotEmpty()) {
                val capitalizedWord =
                    word.substring(0, 1).uppercase(Locale.ROOT) + word.substring(1)
                capitalizedWords.add(capitalizedWord)
            }
        }
        return capitalizedWords.joinToString(separator = " ")
    }
}