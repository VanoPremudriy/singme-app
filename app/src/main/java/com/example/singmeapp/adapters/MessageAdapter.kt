package com.example.singmeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.MessageItemBinding
import com.example.singmeapp.fragments.ChatFragment
import com.example.singmeapp.items.Message
import com.example.singmeapp.viewmodels.ChatViewModel

class MessageAdapter(var fragment: Fragment): RecyclerView.Adapter<MessageAdapter.MessageHolder>() {
    var messageList = ArrayList<Message>()

    class MessageHolder(item: View, var fragment: Fragment): RecyclerView.ViewHolder(item){

        var chatFragment = fragment as ChatFragment
        lateinit var chatViewModel: ChatViewModel
        var binding = MessageItemBinding.bind(item)
        fun bind(message: Message){
            val provider = ViewModelProvider(fragment)
            chatViewModel = provider[ChatViewModel::class.java]
            if (message.senderUuid == chatViewModel.auth.currentUser?.uid){
                binding.llMyMessage.visibility = View.VISIBLE
                binding.tvMyMessage.text = message.message
            }
            else {
                binding.llFriendMessage.visibility = View.VISIBLE
                binding.tvFriendMessage.text = message.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MessageHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}