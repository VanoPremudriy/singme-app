package com.example.singmeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.ChatUserAdapter
import com.example.singmeapp.adapters.FriendAdapter
import com.example.singmeapp.databinding.FragmentMessengerBinding
import com.example.singmeapp.items.ChatUser
import com.example.singmeapp.viewmodels.MessengerViewModel

class MessengerFragment : Fragment() {

    lateinit var binding: FragmentMessengerBinding
    private lateinit var fragActivity: AppCompatActivity

    lateinit var messengerViewModel: MessengerViewModel
    lateinit var chatUsersAdapter: ChatUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragActivity = activity as AppCompatActivity
        val provider = ViewModelProvider(this)
        messengerViewModel = provider[MessengerViewModel::class.java]
        messengerViewModel.getChatUsers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fragActivity.title = getString(R.string.messenger)
        binding = FragmentMessengerBinding.inflate(layoutInflater)

        binding.rcChatUsers.layoutManager = LinearLayoutManager(context)
        chatUsersAdapter = ChatUserAdapter(this)
        messengerViewModel.listChatUsers.observe(viewLifecycleOwner){
            chatUsersAdapter.chatUserList = it as ArrayList<ChatUser> /* = java.util.ArrayList<com.example.singmeapp.items.ChatUser> */
            binding.rcChatUsers.adapter = chatUsersAdapter
        }

        messengerViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["avatarChatUser"] == true){
                binding.messengerProgressLayout.visibility = View.GONE
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MessengerFragment()
    }
}