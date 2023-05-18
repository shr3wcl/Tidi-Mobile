package com.example.tidimobile.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.tidimobile.R
import com.example.tidimobile.model.UserSearchModel
import kotlin.collections.ArrayList

class UserSearchAdapter(private var listUser: ArrayList<UserSearchModel.UserSearchDetail>) :
    RecyclerView.Adapter<UserSearchAdapter.UserSearchViewHolder>() {
    private lateinit var bListener: OnUserSearchClickListener

    interface OnUserSearchClickListener {
        fun onClickUserSearch(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnUserSearchClickListener) {
        bListener = clickListener
    }

    class UserSearchViewHolder(itemView: View, clickListener: OnUserSearchClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickListener.onClickUserSearch(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSearchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_search_item, parent, false)
        return UserSearchViewHolder(view, bListener)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: UserSearchViewHolder, position: Int) {
        holder.itemView.apply {
            findViewById<TextView>(R.id.tv_name_user_search).text =
                listUser[position].firstName + " " + listUser[position].lastName
            if (listUser[position].avatar.toString().isNotEmpty()) {
                try {
                    val imageBytes = Base64.decode(listUser[position].avatar, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    Glide.with(this).load(decodedImage).diskCacheStrategy(
                        DiskCacheStrategy.ALL
                    ).circleCrop().into(findViewById(R.id.image_user_search_item))
                } catch (e: java.lang.Exception) {
                    Toast.makeText(context, "Cannot load image now", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}