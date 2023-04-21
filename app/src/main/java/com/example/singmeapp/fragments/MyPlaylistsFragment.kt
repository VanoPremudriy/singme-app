package com.example.singmeapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.PlaylistAdapter
import com.example.singmeapp.databinding.FragmentMyPlaylistsBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.viewmodels.MyPlaylistsViewModel
import com.example.singmeapp.viewmodels.PlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MyPlaylistsFragment : Fragment(), MenuProvider, View.OnClickListener {

    lateinit var binding: FragmentMyPlaylistsBinding
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var playlistAdapter: PlaylistAdapter

    lateinit var myPlaylistsViewModel: MyPlaylistsViewModel
    lateinit var playlistViewModel: PlaylistViewModel

    lateinit var optionsMenu: Menu
    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val provider = ViewModelProvider(this)
        val playlistProvider = ViewModelProvider(this)
        myPlaylistsViewModel = provider[MyPlaylistsViewModel::class.java]
        playlistViewModel = playlistProvider[PlaylistViewModel::class.java]
        playlistAdapter = PlaylistAdapter(this)
        mainActivity = activity as MainActivity
        fragmentActivity = activity as AppCompatActivity
        myPlaylistsViewModel.getPlaylists()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
        binding = FragmentMyPlaylistsBinding.inflate(layoutInflater)
        fragmentActivity.title = getString(R.string.playlists)
        binding.rcPlaylists.layoutManager = LinearLayoutManager(context)

        observes()

        setButtons()

        return binding.root
    }

    fun observes() {
        myPlaylistsViewModel.listPlaylists.observe(viewLifecycleOwner){
            playlistAdapter.initList(it) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rcPlaylists.adapter = playlistAdapter
        }

        playlistViewModel.isUserPlaylistsChanged.observe(viewLifecycleOwner){
            myPlaylistsViewModel.isAlready.value?.put("image", false)
            binding.myPlaylistsProgressLayout.visibility = View.VISIBLE
            myPlaylistsViewModel.getPlaylists()
        }

        myPlaylistsViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["image"] == true){
                binding.myPlaylistsProgressLayout.visibility = View.GONE
            }
        }
    }

    fun doWhenSort(){
        binding.rcPlaylists.adapter = playlistAdapter
        mainActivity.binding.view15.visibility = View.GONE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun setButtons(){
        mainActivity.binding.tvAddPlaylist.setOnClickListener(this@MyPlaylistsFragment)
        mainActivity.binding.tvSortByDefault.setOnClickListener(this@MyPlaylistsFragment)
        mainActivity.binding.tvSortByDate.setOnClickListener(this@MyPlaylistsFragment)
        mainActivity.binding.tvSortByName.setOnClickListener(this@MyPlaylistsFragment)
        binding.tvSortByInPlaylists.setOnClickListener(this@MyPlaylistsFragment)
    }



    companion object {
        @JvmStatic
        fun newInstance() = MyPlaylistsFragment()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.my_playlists_menu, menu)
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
       when(menuItem.itemId){
           android.R.id.home -> {
               val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
               if (count == 0) {
                   activity?.onBackPressed()

               } else {
                   findNavController().popBackStack()
               }
               return true
           }

           R.id.menu_add_playlist -> {
               mainActivity.binding.AddPlaylistMenu.visibility = View.VISIBLE
               mainActivity.binding.view15.visibility = View.VISIBLE
               mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
               mainActivity.binding.etPlaylistName.text.clear()
           }

           R.id.menu_search_in_playlist -> {
               val searchView = menuItem.actionView as SearchView
               searchView.queryHint = getString(R.string.type_here_to_search)


               searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                   override fun onQueryTextSubmit(query: String?): Boolean {
                       return false
                   }

                   override fun onQueryTextChange(newText: String?): Boolean {
                       playlistAdapter.sortBySearch(newText ?: "")
                       binding.rcPlaylists.adapter = playlistAdapter
                       return true
                   }

               })
           }
       }

        return false
    }


    override fun onClick(p0: View?) {
        when (p0?.id){
            mainActivity.binding.tvAddPlaylist.id -> {
                if (!mainActivity.binding.etPlaylistName.text.isNullOrEmpty()){
                    myPlaylistsViewModel.isPlaylistExist(mainActivity.binding.etPlaylistName.text.toString())
                    mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
                } else {
                    Toast.makeText(context, "Write playlist name", Toast.LENGTH_SHORT).show()
                }

            }

            mainActivity.binding.tvSortByDefault.id -> {
                playlistAdapter.sortByDefault()
                doWhenSort()
            }
            mainActivity.binding.tvSortByDate.id -> {
                playlistAdapter.sortByDate()
                doWhenSort()
            }
            mainActivity.binding.tvSortByName.id -> {
                playlistAdapter.sortByName()
                doWhenSort()
            }
            binding.tvSortByInPlaylists.id -> {
                mainActivity.binding.sortMenu.visibility = View.VISIBLE
                mainActivity.binding.tvSortByAlbum.visibility = View.GONE
                mainActivity.binding.tvSortByBand.visibility = View.GONE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        optionsMenu.clear()
        optionsMenu.close()
    }

}