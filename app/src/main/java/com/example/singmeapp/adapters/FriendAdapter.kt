package com.example.singmeapp.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FriendItemBinding
import com.example.singmeapp.fragments.FriendsFragment
import com.example.singmeapp.items.Friend
import com.example.singmeapp.items.User
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import kotlin.random.Random

class FriendAdapter(val fragment: Fragment): RecyclerView.Adapter<FriendAdapter.FriendHolder>() {

    var friendList = ArrayList<User>()

    class FriendHolder(item: View, val fragment: Fragment): RecyclerView.ViewHolder(item){
        val binding = FriendItemBinding.bind(item)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(friend: User) = with(binding){
            tvFriendItemName.text = friend.name
            tvFriendItemAge.text = friend.age.toString()
            tvFriendItemSex.text = friend.sex
            if (friend.avatarUrl != ""){
                Picasso.get().load(friend.avatarUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivFriendItemAvatar)
            }



            val fr = fragment as FriendsFragment
            var act = fr.activity as MainActivity

            binding.ibFriendItemMenu.setOnClickListener{
                when (friend.friendshipStatus) {
                    "friend" ->{
                        act.binding.friendMenu.visibility = View.VISIBLE
                    }
                    "request" ->{
                        act.binding.requestMenu.visibility = View.VISIBLE
                    }
                    "my request" ->{
                        act.binding.myRequestMenu.visibility = View.VISIBLE
                    }
                }
                act.binding.view15.visibility = View.VISIBLE
                act.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED

            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)
        return FriendHolder(view, fragment)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: FriendHolder, position: Int) {
        holder.bind(friendList[position])

    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}