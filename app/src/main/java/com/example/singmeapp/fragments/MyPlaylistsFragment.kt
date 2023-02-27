package com.example.singmeapp.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
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

    lateinit var bingind: FragmentMyPlaylistsBinding
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
        setHasOptionsMenu(true)
        mainActivity = activity as MainActivity
        fragmentActivity = activity as AppCompatActivity
        myPlaylistsViewModel.getPlaylists()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
        bingind = FragmentMyPlaylistsBinding.inflate(layoutInflater)

        bingind.rcPlaylists.layoutManager = LinearLayoutManager(context)

        myPlaylistsViewModel.listPlaylists.observe(viewLifecycleOwner){
            playlistAdapter.playlistList.clear()
            playlistAdapter.playlistList.addAll(it as ArrayList<Album>) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            bingind.rcPlaylists.adapter = playlistAdapter
        }

        playlistViewModel.isUserPlaylistsChanged.observe(viewLifecycleOwner){
            myPlaylistsViewModel.getPlaylists()
        }

        mainActivity.binding.tvAddPlaylist.setOnClickListener(this)

        return bingind.root
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
        TODO("Not yet implemented")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add_playlist){
            mainActivity.binding.AddPlaylistMenu.visibility = View.VISIBLE
            mainActivity.binding.view15.visibility = View.VISIBLE
            mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            mainActivity.binding.etPlaylistName.text.clear()

        }
        return true
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            mainActivity.binding.tvAddPlaylist.id -> {
                myPlaylistsViewModel.isPlaylistExist(mainActivity.binding.etPlaylistName.text.toString())
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }


}