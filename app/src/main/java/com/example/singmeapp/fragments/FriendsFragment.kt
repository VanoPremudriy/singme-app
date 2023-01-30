package com.example.singmeapp.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.FriendAdapter
import com.example.singmeapp.databinding.FragmentFriendsBinding
import com.example.singmeapp.items.User
import com.example.singmeapp.viewmodels.FriendsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior


class FriendsFragment : Fragment() {

    lateinit var binding: FragmentFriendsBinding
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var friendsViewModel: FriendsViewModel
    lateinit var friendAdapter: FriendAdapter
    lateinit var requestAdapter: FriendAdapter
    lateinit var myRequestAdapter: FriendAdapter
    lateinit var bottomSheetBehavior:BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        fragmentActivity.supportActionBar?.show()
        setHasOptionsMenu(true)
        val provider = ViewModelProvider(this)
        friendsViewModel = provider[FriendsViewModel::class.java]
        friendsViewModel.getFriends()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentActivity.title = getString(R.string.my_friends)
        binding = FragmentFriendsBinding.inflate(layoutInflater)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.view10)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        friendAdapter = FriendAdapter(this)
        requestAdapter = FriendAdapter(this)
        myRequestAdapter = FriendAdapter(this)

        binding.rcMyFriends.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcFriendRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcMyFriendRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        friendsViewModel.listFriends.observe(viewLifecycleOwner){
            friendAdapter.friendList = it as ArrayList<User> /* = java.util.ArrayList<com.example.singmeapp.items.User> */
            binding.rcMyFriends.adapter = friendAdapter
        }

        friendsViewModel.listRequests.observe(viewLifecycleOwner){
            requestAdapter.friendList = it as ArrayList<User> /* = java.util.ArrayList<com.example.singmeapp.items.User> */
            binding.rcFriendRequests.adapter = requestAdapter
        }

        friendsViewModel.listMyRequests.observe(viewLifecycleOwner){
            myRequestAdapter.friendList = it as ArrayList<User> /* = java.util.ArrayList<com.example.singmeapp.items.User> */
            binding.rcMyFriendRequests.adapter = myRequestAdapter
        }



        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
            if (count == 0) {
                (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                activity?.onBackPressed()
            } else {
                (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                findNavController().popBackStack()
            }
        }
        return true
    }

    companion object {

        @JvmStatic
        fun newInstance() = FriendsFragment()
    }


}