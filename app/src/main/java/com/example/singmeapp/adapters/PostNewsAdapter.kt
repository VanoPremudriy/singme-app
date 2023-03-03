package com.example.singmeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R

class PostNewsAdapter: RecyclerView.Adapter<PostNewsAdapter.PostNewsHolder>() {

    val list = ArrayList<String>()

    class PostNewsHolder(item: View): RecyclerView.ViewHolder(item){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostNewsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_news_item, parent, false)
        return PostNewsHolder(view)
    }

    override fun onBindViewHolder(holder: PostNewsHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return  list.size
    }
}