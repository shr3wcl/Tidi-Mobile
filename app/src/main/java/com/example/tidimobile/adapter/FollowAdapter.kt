package com.example.tidimobile.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tidimobile.R
import com.example.tidimobile.model.CommentModel
import com.example.tidimobile.model.FollowModelGet
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FollowAdapter(private var listFollow: ArrayList<FollowModelGet.FollowersData>) :
    RecyclerView.Adapter<FollowAdapter.FollowViewHolder>() {

    private lateinit var cListener: OnFlwClickListener

    interface OnFlwClickListener {
        fun onClickFlw(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnFlwClickListener) {
        cListener = clickListener
    }

    class FollowViewHolder(itemView: View, clickListener: OnFlwClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickListener.onClickFlw(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item_ov, parent, false)
        return FollowViewHolder(view, cListener)
    }

    override fun getItemCount(): Int {
        return listFollow.size
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.txtNameUserItemOV).text =
                listFollow[position].idFollow?.firstName + " " + listFollow[position].idFollow?.lastName
            val rawDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val date: Date = listFollow[position].idFollow?.createdAt?.let { rawDateFormat.parse(it) } as Date
            val outputFormat = SimpleDateFormat("HH:mm:ss - dd/MM/yyyy")
            val dateFormat = outputFormat.format(date)
            findViewById<TextView>(R.id.dateLiked).text = dateFormat
        }
    }
}