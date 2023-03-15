package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
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


class FriendsFragment : Fragment(), MenuProvider {

    lateinit var binding: FragmentFriendsBinding
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var friendsViewModel: FriendsViewModel
    lateinit var friendAdapter: FriendAdapter
    lateinit var requestAdapter: FriendAdapter
    lateinit var myRequestAdapter: FriendAdapter
    lateinit var optionsMenu: Menu
    lateinit var userAdapter: FriendAdapter
    var arrayListUsers = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        fragmentActivity.supportActionBar?.show()
        setHasOptionsMenu(true)
        val provider = ViewModelProvider(this)
        friendsViewModel = provider[FriendsViewModel::class.java]
        if (arguments?.getSerializable("curUser") != null)
            if ((arguments?.getSerializable("curUser") as User).uuid != friendsViewModel.auth.currentUser?.uid.toString()){
                friendsViewModel.getOtherFriends((requireArguments().getSerializable("curUser") as User).uuid.toString())
            }else friendsViewModel.getFriends()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentActivity.title = getString(R.string.my_friends)
        binding = FragmentFriendsBinding.inflate(layoutInflater)

        if ((arguments?.getSerializable("curUser") as User).uuid != friendsViewModel.auth.currentUser?.uid.toString()){
            binding.tvMyRequests.text = getString(R.string.user_requests)
            binding.tvMyFriendsInFriendsFragment.text = getString(R.string.friends)
        }


        friendAdapter = FriendAdapter(this)
        requestAdapter = FriendAdapter(this)
        myRequestAdapter = FriendAdapter(this)
        userAdapter = FriendAdapter(this)

        binding.rcMyFriends.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcFriendRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcMyFriendRequests.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcAllUsers.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        friendsViewModel.listFriends.observe(viewLifecycleOwner){
            friendAdapter.friendList.clear()
            friendAdapter.friendList.addAll(it as ArrayList<User>) /* = java.util.ArrayList<com.example.singmeapp.items.User> */
            binding.rcMyFriends.adapter = friendAdapter
            binding.tvMyFriendsInFriendsFragmentCount.text = "(${it.size})"
        }

        friendsViewModel.listRequests.observe(viewLifecycleOwner){
            requestAdapter.friendList.clear()
            requestAdapter.friendList.addAll(it as ArrayList<User>)
            binding.rcFriendRequests.adapter = requestAdapter
            binding.tvRequestsCount.text = "(${it.size})"
        }

        friendsViewModel.listMyRequests.observe(viewLifecycleOwner){
            myRequestAdapter.friendList.clear()
            myRequestAdapter.friendList.addAll(it as ArrayList<User>)
            binding.rcMyFriendRequests.adapter = myRequestAdapter
            binding.tvMyRequestsCount.text = "(${it.size})"
        }

        friendsViewModel.listAllUsers.observe(viewLifecycleOwner){
            arrayListUsers.clear()
            arrayListUsers.addAll(it as ArrayList<User>)
            userAdapter.friendList.clear()
            userAdapter.friendList.addAll(it as ArrayList<User>) /* = java.util.ArrayList<com.example.singmeapp.items.User> */
            binding.rcAllUsers.adapter = userAdapter
        }

        friendsViewModel.isAlready.observe(viewLifecycleOwner){
            if ((it["avatarFriend"] == true && it["avatarRequest"] == true && it["avatarMyRequest"] == true) || it["avatarUser"] == true){
                binding.friendsProgressLayout.visibility = View.GONE
            }
        }



        binding.bWrapMyFriendRequests.setOnClickListener{
            wrap(binding.rcMyFriendRequests)
        }
        binding.bWrapFriendRequests.setOnClickListener{
            wrap(binding.rcFriendRequests)
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

        if (item.itemId == R.id.action_search_user){
            item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    Log.e("Search", "onMenuItemActionExpand")
                    friendsViewModel.isAlready.value?.put("avatarUser", false)
                    friendsViewModel.getAllUsers()
                    binding.friendsProgressLayout.visibility = View.VISIBLE
                    binding.searchLayout.visibility = View.VISIBLE
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    binding.searchLayout.visibility = View.GONE
                    (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    return true
                }

            })

            val searcView = item.actionView as SearchView
            searcView.queryHint = "Type here to search"

            searcView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.e("Search", "onQueryTextSubmit")
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.e("Search", "onQueryTextChange")
                    userAdapter.friendList = arrayListUsers.filter {
                         (it.name.contains(newText!!, ignoreCase = true))
                    } as ArrayList<User> /* = java.util.ArrayList<com.example.singmeapp.items.User> */
                    binding.rcAllUsers.adapter = userAdapter
                    return true
                }

            })

        }
        return true
    }

    private fun convert(value: Int):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()
    }

    private fun wrap(wrapItem: View){
        if (wrapItem.height == convert(0)) {
            Log.e("efs", "YES")
            val params = wrapItem.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            //binding.rcVievMembers.adapter = memberAdapter
            wrapItem.layoutParams = params
        }
        else {
            val params = wrapItem.layoutParams
            params.height = convert(0)
            wrapItem.layoutParams = params
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = FriendsFragment()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        TODO("Not yet implemented")
    }


}