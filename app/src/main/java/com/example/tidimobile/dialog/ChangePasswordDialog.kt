package com.example.tidimobile.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.tidimobile.R

class ChangePasswordDialog(context: Context, private val listener: PasswordDialogListener) : Dialog(context) {

    private lateinit var currentPassword: String
    private lateinit var newPassword: String
    private lateinit var confirmNewPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password_dialog)
        val saveButton = findViewById<Button>(R.id.save_btn)

        saveButton.setOnClickListener {
            currentPassword = findViewById<EditText>(R.id.txtCurrentPassword).text.toString()
            newPassword = findViewById<EditText>(R.id.txtNewPassword).text.toString()
            confirmNewPassword = findViewById<EditText>(R.id.txtConfirmNewPassword).text.toString()
            listener.onChangePasswordSelected(currentPassword, newPassword, confirmNewPassword)

            dismiss()
        }
    }



    interface PasswordDialogListener {
        fun onChangePasswordSelected(currentPassword: String, newPassword: String, confirmNewPassword: String)
    }
}