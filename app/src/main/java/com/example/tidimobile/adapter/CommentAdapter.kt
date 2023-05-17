package com.example.tidimobile.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tidimobile.R
import com.example.tidimobile.model.CommentModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.util.Base64
import android.widget.ImageView

class CommentAdapter(private var listComment: ArrayList<CommentModel>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private lateinit var cListener: OnCmtClickListener

    interface OnCmtClickListener {
        fun onClickCmt(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnCmtClickListener) {
        cListener = clickListener
    }

    class CommentViewHolder(itemView: View, clickListener: OnCmtClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickListener.onClickCmt(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view, cListener)
    }

    override fun getItemCount(): Int {
        return listComment.size
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.nameUserCmt).text =
                "${listComment[position].idUser?.firstName} ${listComment[position].idUser?.lastName}"

            val rawDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val date: Date =
                listComment[position].createdAt?.let { rawDateFormat.parse(it) } as Date
            val outputFormat = SimpleDateFormat("HH:mm:ss - dd/MM/yyyy")
            val dateFormat = outputFormat.format(date)

            val img = findViewById<ImageView>(R.id.avatarCmt)

            val imageBytes = Base64.decode(listComment[position].idUser?.avatar, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            Glide.with(this).load(decodedImage).diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).circleCrop().into(img)

            findViewById<TextView>(R.id.timestampCmt).text = dateFormat
            findViewById<TextView>(R.id.cmtTv).text = listComment[position].content
        }
    }
}