package com.example.tidimobile.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tidimobile.R
import com.example.tidimobile.model.BlogModelBasic
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BlogsAdapter(private var listBlogs: ArrayList<BlogModelBasic.BlogBasicObject>) :
    RecyclerView.Adapter<BlogsAdapter.BlogViewHolder>() {
    private lateinit var bListener: OnBlogClickListener

    interface OnBlogClickListener {
        fun onClickBlog(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnBlogClickListener) {
        bListener = clickListener
    }

    class BlogViewHolder(itemView: View, clickListener: OnBlogClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickListener.onClickBlog(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.blog_item_layout, parent, false)
        return BlogViewHolder(view, bListener)
    }

    override fun getItemCount(): Int {
        return listBlogs.size
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.itemView.apply {
            val tvTitle = findViewById<TextView>(R.id.txtTitleBlog)
            val tvDescription = findViewById<TextView>(R.id.txtDescriptionBlog)
            val tvAuthor = findViewById<TextView>(R.id.txtAuthor)
            val tvTime = findViewById<TextView>(R.id.txtTimeCreated)
            val tvStatus = findViewById<TextView>(R.id.txtStatus)
            val rawDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val date: Date = listBlogs[position].createdAt?.let { rawDateFormat.parse(it) } as Date
            val outputFormat = SimpleDateFormat("HH:mm:ss - dd/MM/yyyy")
            val dateFormat = outputFormat.format(date)
            tvTitle.text = listBlogs[position].title
            tvDescription.text = listBlogs[position].description
            tvAuthor.text =
                "${listBlogs[position].idUser?.firstName} ${listBlogs[position].idUser?.lastName}"
            tvTime.text = dateFormat
            if (listBlogs[position].status == true) {
                tvStatus.text = "Public"
            } else {
                tvStatus.text = "Private"
                tvStatus.setBackgroundColor(Color.RED)
            }
        }
    }

}