package com.example.tidimobile.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.example.tidimobile.R
import com.example.tidimobile.model.BlogModel

class BlogDialog(context: Context, private val listener: BlogDialogListener, private val titleInit: String, private val desInit: String, val dataBlog: BlogModel.BlogObject.ContentObject) : Dialog(context) {

    private lateinit var title: String
    private lateinit var description: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blog_dialog)
        val saveButton = findViewById<Button>(R.id.save_btn)
        title = titleInit
        description = desInit
        findViewById<EditText>(R.id.txtDescriptionBlog).setText(description)
        findViewById<EditText>(R.id.txtEditNameBlogEdit).setText(title)
        saveButton.setOnClickListener {
            val titleBlog = findViewById<EditText>(R.id.txtEditNameBlogEdit).text.toString()
            val desBlog = findViewById<EditText>(R.id.txtDescriptionBlog).text.toString()
            title = titleBlog
            description = desBlog
            listener.onBlogSelected(titleBlog, desBlog, dataBlog)

            dismiss()
        }
    }



    interface BlogDialogListener {
        fun onBlogSelected(title: String, description: String, dataBlog: BlogModel.BlogObject.ContentObject)
    }
}