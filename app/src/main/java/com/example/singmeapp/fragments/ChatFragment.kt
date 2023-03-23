package com.example.singmeapp.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.MessageAdapter
import com.example.singmeapp.databinding.FragmentChatBinding
import com.example.singmeapp.databinding.FragmentCreateBandBinding
import com.example.singmeapp.items.ChatUser
import com.example.singmeapp.items.Message
import com.example.singmeapp.viewmodels.ChatViewModel


class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    lateinit var chatViewModel: ChatViewModel
    lateinit var messageAdapter: MessageAdapter
    lateinit var chatUser: ChatUser
    lateinit var fragmentActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        fragmentActivity = (activity as AppCompatActivity)
        val provider = ViewModelProvider(this)
        chatViewModel = provider[ChatViewModel::class.java]
        chatUser = arguments?.getSerializable("chatUser") as ChatUser
        chatViewModel.getMessages(chatUser)
        (activity as MainActivity).binding.bNav.visibility = View.GONE
        (activity as MainActivity).binding.player.root.visibility = View.INVISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentActivity.title = chatUser.user.name
        binding = FragmentChatBinding.inflate(layoutInflater)
        messageAdapter = MessageAdapter(this)
        binding.rcChat.layoutManager = LinearLayoutManager(context)
        chatViewModel.listMessages.observe(viewLifecycleOwner){
            messageAdapter.messageList = it as ArrayList<Message>
            binding.rcChat.adapter = messageAdapter
            (binding.rcChat.layoutManager as LinearLayoutManager).scrollToPosition(it.size-1)
            chatViewModel.readMessages(chatUser)
        }
        binding.imageButton2.setOnClickListener{
            if (binding.multiAutoCompleteTextView.text.toString() != "" && binding.multiAutoCompleteTextView.text.toString() != null){
                chatViewModel.sendMessage(binding.multiAutoCompleteTextView.text.toString(), chatUser)
            }
            binding.multiAutoCompleteTextView.text.clear()
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            //findNavController().navigate(R.id.myLibraryFragment)
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

            if (count == 0) {
                (activity as AppCompatActivity).supportActionBar?.show()
                (activity as MainActivity).binding.bNav.visibility = View.VISIBLE
                if ((activity as MainActivity).binding.root.visibility == View.INVISIBLE){
                    (activity as MainActivity).binding.root.visibility = View.VISIBLE
                }
                activity?.onBackPressed()

            } else {
                (activity as AppCompatActivity).supportActionBar?.show()
                (activity as MainActivity).binding.bNav.visibility = View.VISIBLE
                if ((activity as MainActivity).binding.player.root.visibility == View.INVISIBLE){
                    (activity as MainActivity).binding.player.root.visibility = View.VISIBLE
                }
                findNavController().popBackStack()
            }
        }
        return true
    }

    companion object {

        @JvmStatic
        fun newInstance() = ChatFragment()
    }
}