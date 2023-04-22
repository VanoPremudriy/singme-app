package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.View.OnClickListener
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


class FriendsFragment : Fragment(), MenuProvider, OnClickListener {

    lateinit var binding: FragmentFriendsBinding
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var friendsViewModel: FriendsViewModel
    lateinit var friendAdapter: FriendAdapter
    lateinit var requestAdapter: FriendAdapter
    lateinit var myRequestAdapter: FriendAdapter
    lateinit var optionsMenu: Menu
    lateinit var userAdapter: FriendAdapter
    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        mainActivity = activity as MainActivity
        fragmentActivity.supportActionBar?.show()
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


        observse()
        setButtons()



        // Inflate the layout for this fragment
        return binding.root
    }

    fun observse(){
        friendsViewModel.listFriends.observe(viewLifecycleOwner){
            friendAdapter.initList(it)
            binding.rcMyFriends.adapter = friendAdapter
            binding.tvMyFriendsInFriendsFragmentCount.text = "(${it.size})"
        }

        friendsViewModel.listRequests.observe(viewLifecycleOwner){
            requestAdapter.initList(it)
            binding.rcFriendRequests.adapter = requestAdapter
            binding.tvRequestsCount.text = "(${it.size})"
        }

        friendsViewModel.listMyRequests.observe(viewLifecycleOwner){
            myRequestAdapter.initList(it)
            binding.rcMyFriendRequests.adapter = myRequestAdapter
            binding.tvMyRequestsCount.text = "(${it.size})"
        }

        friendsViewModel.listAllUsers.observe(viewLifecycleOwner){
            userAdapter.initList(it)
            binding.rcAllUsers.adapter = userAdapter
        }

        friendsViewModel.isAlready.observe(viewLifecycleOwner){
            if ((it["avatarFriend"] == true && it["avatarRequest"] == true && it["avatarMyRequest"] == true) || it["avatarUser"] == true){
                binding.friendsProgressLayout.visibility = View.GONE
            }
        }
    }

    fun setButtons(){
        binding.bWrapMyFriendRequests.setOnClickListener(this@FriendsFragment)
        binding.bWrapFriendRequests.setOnClickListener(this@FriendsFragment)
        binding.tvSortByInFriends.setOnClickListener(this@FriendsFragment)
        mainActivity.binding.tvSortUsersByName.setOnClickListener(this@FriendsFragment)
        mainActivity.binding.tvSortUsersByDefault.setOnClickListener(this@FriendsFragment)
        mainActivity.binding.tvSortUsersByAge.setOnClickListener(this@FriendsFragment)
        mainActivity.binding.tvSortUsersBySex.setOnClickListener(this@FriendsFragment)
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
        when(menuItem.itemId){
            android.R.id.home -> {
                val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
                if (count == 0) {
                    (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    activity?.onBackPressed()
                } else {
                    (activity as MainActivity).bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                    findNavController().popBackStack()
                }
                return true
            }

            R.id.action_search_user -> {
                menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
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

                val searcView = menuItem.actionView as SearchView
                searcView.queryHint = "Type here to search"

                searcView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.e("Search", "onQueryTextSubmit")
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        userAdapter.sortBySearch(newText ?: "")
                        binding.rcAllUsers.adapter = userAdapter
                        return true
                    }

                })
                return true
            }

        }

        return false
    }

    fun doWhenSort(){
        binding.rcMyFriends.adapter  = friendAdapter
        mainActivity.binding.view15.visibility = View.GONE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onDestroyView() {
        super.onDestroyView()
        optionsMenu.clear()
        optionsMenu.close()
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            binding.bWrapMyFriendRequests.id -> {
                wrap(binding.rcMyFriendRequests)
            }

            binding.bWrapFriendRequests.id -> {
                wrap(binding.rcFriendRequests)
            }

            binding.tvSortByInFriends.id -> {
                mainActivity.binding.userSortMenu.visibility = View.VISIBLE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }

            mainActivity.binding.tvSortUsersByName.id -> {
                friendAdapter.sortByName()
                doWhenSort()
            }

            mainActivity.binding.tvSortUsersByDefault.id -> {
                friendAdapter.sortByDefault()
                doWhenSort()
            }

            mainActivity.binding.tvSortUsersByAge.id -> {
                friendAdapter.sortByAge()
                doWhenSort()
            }

            mainActivity.binding.tvSortUsersBySex.id -> {
                friendAdapter.sortBySex()
                doWhenSort()
            }
        }
    }


}