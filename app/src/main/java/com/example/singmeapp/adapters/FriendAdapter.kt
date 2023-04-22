package com.example.singmeapp.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FriendItemBinding
import com.example.singmeapp.fragments.FriendsFragment
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.ChatUser
import com.example.singmeapp.items.User
import com.example.singmeapp.viewmodels.FriendsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso

class FriendAdapter(val fragment: Fragment): RecyclerView.Adapter<FriendAdapter.FriendHolder>() {

    var friendList = ArrayList<User>()
    var friendListDefaultCopy = ArrayList<User>()

    class FriendHolder(item: View, val fragment: Fragment): RecyclerView.ViewHolder(item), View.OnClickListener{
        val binding = FriendItemBinding.bind(item)
        val friendsFragment = fragment as FriendsFragment
        val activity = friendsFragment.activity as MainActivity
        val provider  = ViewModelProvider(fragment)
        var friendsViewModel = provider[FriendsViewModel::class.java]
        lateinit var curFriend: User

        @SuppressLint("ClickableViewAccessibility")
        fun bind(friend: User) = with(binding){
            curFriend = friend
            tvFriendItemName.text = friend.name
            tvFriendItemAge.text = friend.age.toString()
            tvFriendItemSex.text = if (friend.sex == "male") fragment.getString(R.string.male) else fragment.getString(R.string.female)
            if (friend.avatarUrl != ""){
                Picasso.get().load(friend.avatarUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivFriendItemAvatar)
            }


            binding.ibFriendItemMenu.setOnClickListener(this@FriendHolder)
            binding.ibFriendItemMenu.setOnClickListener {
                when (curFriend.friendshipStatus) {
                    "friend" ->{
                        activity.binding.friendMenu.visibility = View.VISIBLE
                    }
                    "request" ->{
                        activity.binding.requestMenu.visibility = View.VISIBLE
                    }
                    "my request" ->{
                        activity.binding.myRequestMenu.visibility = View.VISIBLE
                    }
                    "unknown" ->{
                        activity.binding.unknownUserMenu.visibility = View.VISIBLE
                    }
                    "me" -> {
                        activity.binding.meMenu.visibility = View.VISIBLE
                    }
                }
                activity.binding.view15.visibility = View.VISIBLE
                activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED

                activity.binding.tvGoToMyProfile.setOnClickListener(this@FriendHolder)

                activity.binding.tvSendFriendshipRequest.setOnClickListener(this@FriendHolder)
                activity.binding.tvGoToUnknownUserProfile.setOnClickListener(this@FriendHolder)
                activity.binding.tvGoToUnknownUserChat.setOnClickListener(this@FriendHolder)

                activity.binding.tvGoToFriendProfile.setOnClickListener(this@FriendHolder)
                activity.binding.tvGoToFriendChat.setOnClickListener(this@FriendHolder)
                activity.binding.tvDeleteFriend.setOnClickListener(this@FriendHolder)

                activity.binding.tvApplyRequest.setOnClickListener(this@FriendHolder)
                activity.binding.tvCancelRequest.setOnClickListener(this@FriendHolder)
                activity.binding.tvGoToRequestProfile.setOnClickListener(this@FriendHolder)
                activity.binding.tvGoToRequestChat.setOnClickListener(this@FriendHolder)

                activity.binding.tvCancelMyRequest.setOnClickListener(this@FriendHolder)
                activity.binding.tvGoToMyRequestProfile.setOnClickListener(this@FriendHolder)
                activity.binding.tvGoToMyRequestChat.setOnClickListener(this@FriendHolder)
            }

            binding.ibFriendItemChat.setOnClickListener(this@FriendHolder)

        }

        override fun onClick(p0: View?) {
            when (p0?.id){

                binding.ibFriendItemChat.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    val chatUser = ChatUser(curFriend, null)
                    val bundle = Bundle()
                    bundle.putSerializable("chatUser", chatUser)
                    friendsFragment.findNavController().navigate(R.id.chatFragment, bundle)
                }

                activity.binding.tvGoToMyProfile.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    friendsFragment.findNavController().navigate(R.id.profileFragment)
                }

                activity.binding.tvSendFriendshipRequest.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    Log.e("Click", curFriend.name)
                    friendsViewModel.sendRequest(curFriend.uuid)
                }
                activity.binding.tvGoToUnknownUserProfile.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    val bundle = Bundle()
                    bundle.putSerializable("otherUser", curFriend)
                    friendsFragment.findNavController().navigate(R.id.profileFragment, bundle)
                }
                activity.binding.tvGoToUnknownUserChat.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    val chatUser = ChatUser(curFriend, null)
                    val bundle = Bundle()
                    bundle.putSerializable("chatUser", chatUser)
                    friendsFragment.findNavController().navigate(R.id.chatFragment, bundle)
                }


                activity.binding.tvGoToFriendProfile.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    val bundle = Bundle()
                    bundle.putSerializable("otherUser", curFriend)
                    friendsFragment.findNavController().navigate(R.id.profileFragment, bundle)
                }
                activity.binding.tvGoToFriendChat.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    val chatUser = ChatUser(curFriend, null)
                    val bundle = Bundle()
                    bundle.putSerializable("chatUser", chatUser)
                    friendsFragment.findNavController().navigate(R.id.chatFragment, bundle)
                }
                activity.binding.tvDeleteFriend.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    friendsViewModel.deleteFriend(curFriend.uuid)
                }


                activity.binding.tvApplyRequest.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    friendsViewModel.applyRequest(curFriend.uuid)
                }
                activity.binding.tvCancelRequest.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    friendsViewModel.cancelRequest(curFriend.uuid)
                }
                activity.binding.tvGoToRequestProfile.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    val bundle = Bundle()
                    bundle.putSerializable("otherUser", curFriend)
                    friendsFragment.findNavController().navigate(R.id.profileFragment, bundle)
                }
                activity.binding.tvGoToRequestChat.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    val chatUser = ChatUser(curFriend, null)
                    val bundle = Bundle()
                    bundle.putSerializable("chatUser", chatUser)
                    friendsFragment.findNavController().navigate(R.id.chatFragment, bundle)
                }


                activity.binding.tvCancelMyRequest.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    friendsViewModel.cancelMyRequest(curFriend.uuid)
                }
                activity.binding.tvGoToMyRequestProfile.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    val bundle = Bundle()
                    bundle.putSerializable("otherUser", curFriend)
                    friendsFragment.findNavController().navigate(R.id.profileFragment, bundle)
                }
                activity.binding.tvGoToMyRequestChat.id -> {
                    activity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    val chatUser = ChatUser(curFriend, null)
                    val bundle = Bundle()
                    bundle.putSerializable("chatUser", chatUser)
                    friendsFragment.findNavController().navigate(R.id.chatFragment, bundle)
                }
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

    fun initList(users: List<User>){
        friendList.clear()
        friendListDefaultCopy.clear()
        friendList.addAll(users)
        friendListDefaultCopy.addAll(users)
    }

    fun sortByDefault(){
        friendList.clear()
        friendList.addAll(friendListDefaultCopy)
    }

    fun sortByName(){
        friendList.sortBy{
            user: User -> user.name
        }
    }

    fun sortByAge(){
        friendList.sortBy {
            user: User -> user.age
        }
    }

    fun sortBySex(){
        friendList.sortBy {
            user: User -> user.sex
        }
    }

    fun sortBySearch(search: String){
        friendList.clear()
        friendList.addAll(friendListDefaultCopy.filter { friend: User -> friend.name.lowercase().contains(search.lowercase()) })
    }

}