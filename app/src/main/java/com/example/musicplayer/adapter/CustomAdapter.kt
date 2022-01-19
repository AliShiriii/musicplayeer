package com.example.musicplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.musicplayer.R
import com.example.musicplayer.view.HomeFragment

class CustomAdapter: BaseAdapter() {

    override fun getCount(): Int {

        return HomeFragment.items.size

    }

    override fun getItem(p0: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(p0: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {

        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.list_item, null)

        val textSong: TextView = view.findViewById(R.id.textSong)
        val imageSong: ImageView = view.findViewById(R.id.imageSong)

        textSong.isSelected = true
        textSong.text = HomeFragment.items[position]
        return view
    }
}