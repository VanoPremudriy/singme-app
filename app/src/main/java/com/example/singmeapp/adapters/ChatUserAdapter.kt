package com.example.singmeapp.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.ChatUserItemBinding
import com.example.singmeapp.fragments.MessengerFragment
import com.example.singmeapp.items.ChatUser
import com.squareup.picasso.Picasso
import java.time.format.DateTimeFormatter

class ChatUserAdapter(var fragment: Fragment): RecyclerView.Adapter<ChatUserAdapter.ChatUserHolder>() {

    var chatUserList = ArrayList<ChatUser>()

    class ChatUserHolder(item: View, var fragment: Fragment): RecyclerView.ViewHolder(item){
        var messengerFragment = fragment as MessengerFragment
        val binding = ChatUserItemBinding.bind(item)
        @RequiresApi(Build.VERSION_CODES.O)
        var formatterTime: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("ResourceAsColor")
        fun bind(chatUser: ChatUser){
            if (chatUser.user.avatarUrl != ""){
                Picasso.get().load(chatUser.user.avatarUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivItemChatUserAvatar)
            }
            binding.tvChatUserName.text = chatUser.user.name
            binding.tvChatUserLastMessage.text = chatUser.lastMessage?.message
            binding.tvChatUserMessageTime.text = chatUser.lastMessage?.dateTime?.format(formatterTime)
                .toString()
            if (chatUser.lastMessage?.isReaded == false){
                Log.e("uuid", chatUser.lastMessage!!.senderUuid.toString())
                Log.e("uuid1", chatUser.user.uuid)
                if (chatUser.lastMessage!!.senderUuid == chatUser.user.uuid){
                    //binding.tvChatUserLastMessage.setBackgroundResource(R.color.red)
                    binding.view16.visibility = View.VISIBLE
                }
                else {
                    binding.tvChatUserLastMessage.setBackgroundResource(R.color.purple_1000)
                }
            }

            binding.chatUserItemLayout.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("chatUser", chatUser)
                messengerFragment.findNavController().navigate(R.id.chatFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.chat_user_item, parent, false)
        return ChatUserHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: ChatUserHolder, position: Int) {
       holder.bind(chatUserList[position])
    }

    override fun getItemCount(): Int {
        return chatUserList.size
    }
}