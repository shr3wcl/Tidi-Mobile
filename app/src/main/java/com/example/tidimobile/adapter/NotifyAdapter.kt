package com.example.tidimobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tidimobile.R
import com.example.tidimobile.model.NotifyModel

class NotifyAdapter(private var listNotify: ArrayList<NotifyModel.SubNotifyModel>) :
    RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder>() {
    private lateinit var nListener: OnClickNotifyListener

    interface OnClickNotifyListener {
        fun onClickNotify(position: Int)
    }

    fun setOnClickItemListener(clickListener: OnClickNotifyListener) {
        nListener = clickListener
    }

    class NotifyViewHolder(itemView: View, clickListener: OnClickNotifyListener) :
        RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                clickListener.onClickNotify(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.notify_item_layout, parent, false)
        return NotifyViewHolder(view, nListener)
    }

    override fun getItemCount(): Int {
        return listNotify.size
    }

    override fun onBindViewHolder(holder: NotifyViewHolder, position: Int) {
        holder.itemView.apply {
            val title = findViewById<TextView>(R.id.item_title)
            val des = findViewById<TextView>(R.id.item_description)
            title.text = listNotify[position].content
            des.text = listNotify[position].content
        }
    }
}