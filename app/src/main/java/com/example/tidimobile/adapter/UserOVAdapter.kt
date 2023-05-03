package com.example.tidimobile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tidimobile.R
import com.example.tidimobile.model.BlogOverviewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserOVAdapter(private var listUser: ArrayList<BlogOverviewModel.LikeOVModel>) :
    RecyclerView.Adapter<UserOVAdapter.UserOVViewHolder>() {
    private lateinit var uListener: OnUserOVClickListener

    interface OnUserOVClickListener {
        fun onClickUser(position: Int)
    }

    fun setOnItemClickListener(clickListener: OnUserOVClickListener) {
        uListener = clickListener
    }

    class UserOVViewHolder(itemView: View, clickListener: OnUserOVClickListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickListener.onClickUser(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserOVViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item_ov, parent, false)
        return UserOVViewHolder(view, uListener)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: UserOVViewHolder, position: Int) {
        holder.itemView.apply {
            val tvTitle = findViewById<TextView>(R.id.txtNameUserItemOV)
            val tvDate = findViewById<TextView>(R.id.dateLiked)

            tvTitle.text =
                "${listUser[position].idUser?.firstName} ${listUser[position].idUser?.lastName}"
            val rawDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val date: Date = listUser[position].createdAt?.let { rawDateFormat.parse(it) } as Date
            val outputFormat = SimpleDateFormat("HH:mm:ss - dd/MM/yyyy")
            val dateFormat = outputFormat.format(date)
            tvDate.text = dateFormat
        }
    }
}