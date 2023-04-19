package com.example.tidimobile.dialog

import android.widget.EditText
import com.example.tidimobile.R
import com.example.tidimobile.model.BlogModel
import kotlin.properties.Delegates
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import com.example.tidimobile.model.ContentObject

class BlogDialog(context: Context, private val listener: BlogDialogListener, private val titleInit: String, private val desInit: String, private val dataBlog: ContentObject, private val statusBlog: Boolean) : Dialog(context) {

    private lateinit var title: String
    private lateinit var description: String
    private lateinit var statusRadioGroup: RadioGroup

    private var status by Delegates.notNull<Boolean>()
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
            statusRadioGroup = findViewById(R.id.status_radio_group)
            title = titleBlog
            description = desBlog
            status = when(statusRadioGroup.checkedRadioButtonId){
                R.id.public_option -> true
                R.id.private_option -> false
                else -> true
            }
            listener.onBlogSelected(titleBlog, desBlog, dataBlog, statusBlog)

            dismiss()
        }
    }



    interface BlogDialogListener {
        fun onBlogSelected(title: String, description: String, dataBlog: ContentObject, statusBlog: Boolean)
    }
}