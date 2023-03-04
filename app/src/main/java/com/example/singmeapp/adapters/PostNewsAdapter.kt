package com.example.singmeapp.adapters

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.PostNewsItemBinding
import com.example.singmeapp.items.Post
import com.squareup.picasso.Picasso
import java.time.format.DateTimeFormatter

class PostNewsAdapter(val fragment: Fragment): RecyclerView.Adapter<PostNewsAdapter.PostNewsHolder>() {

    val list = ArrayList<Post>()

    class PostNewsHolder(item: View, val fragment: Fragment): RecyclerView.ViewHolder(item){
        val binding = PostNewsItemBinding.bind(item)
        @RequiresApi(Build.VERSION_CODES.O)
        var formatterTime: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        @RequiresApi(Build.VERSION_CODES.O)
        var formatterDate: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(post: Post){
            binding.tvBandNameInPost.text = post.band.name
            binding.tvDateInPost.text = post.dateTime.format(formatterTime).toString()
            binding.tvDateTwoInPost.text = post.dateTime.format(formatterDate).toString()
            binding.tvItemAlbumNameInPost.text = post.album.name
            if (post.album.imageUrl != "") {
                Picasso.get().load(post.album.imageUrl).fit().into(binding.ivAlbumCoverInPost)
                Picasso.get().load(post.album.imageUrl).fit().into(binding.ivItemAlbumCoverInPost)
            }
            if (post.band.imageUrl != ""){
                Picasso.get().load(post.band.imageUrl).fit().into(binding.ivBandAvatar)
            }

            binding.llAlbumInPost.setOnClickListener{
                val bundle = Bundle()
                bundle.putSerializable("albumUuid", post.album.uuid)
                NavHostFragment.findNavController(fragment).navigate(R.id.albumFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostNewsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_news_item, parent, false)
        return PostNewsHolder(view, fragment)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostNewsHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}