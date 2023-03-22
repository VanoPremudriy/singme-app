package com.example.singmeapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.MessageItemBinding
import com.example.singmeapp.fragments.ChatFragment
import com.example.singmeapp.items.Message
import com.example.singmeapp.viewmodels.ChatViewModel
import java.time.format.DateTimeFormatter

class MessageAdapter(var fragment: Fragment): RecyclerView.Adapter<MessageAdapter.MessageHolder>() {
    var messageList = ArrayList<Message>()


    class MessageHolder(item: View, var fragment: Fragment): RecyclerView.ViewHolder(item){
        @RequiresApi(Build.VERSION_CODES.O)
        var formatterTime: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        var chatFragment = fragment as ChatFragment
        lateinit var chatViewModel: ChatViewModel
        var binding = MessageItemBinding.bind(item)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(message: Message){
            val provider = ViewModelProvider(fragment)
            chatViewModel = provider[ChatViewModel::class.java]
            if (message.senderUuid == chatViewModel.auth.currentUser?.uid){
                binding.llMyMessage.visibility = View.VISIBLE
                binding.tvMyMessage.text = message.message
                binding.tvMyMessageTime.text = message.dateTime.format(formatterTime).toString()
                if (!message.isReaded){
                    binding.llMyMessage.setBackgroundResource(R.color.purple_1000)
                }
            }
            else {
                binding.llFriendMessage.visibility = View.VISIBLE
                binding.tvFriendMessage.text = message.message
                binding.tvFriendMessageTime.text = message.dateTime.format(formatterTime).toString()
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