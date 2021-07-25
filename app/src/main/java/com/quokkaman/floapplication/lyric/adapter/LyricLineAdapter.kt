package com.quokkaman.floapplication.lyric.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quokkaman.floapplication.R
import com.quokkaman.floapplication.model.LyricLine

class LyricLineAdapter : ListAdapter<LyricLine, LyricLineAdapter.ViewHolder>(diffUtil){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_lyric_line, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        private val textView: TextView = item.findViewById(R.id.tv_lyricline)

        fun bind(lyricLine: LyricLine) {
            textView.text = lyricLine.lyrics
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<LyricLine>() {
            override fun areContentsTheSame(oldItem: LyricLine, newItem: LyricLine) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: LyricLine, newItem: LyricLine) =
                oldItem.msec == newItem.msec
        }
    }
}