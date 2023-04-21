package com.example.singmeapp.fragments

import android.os.Bundle
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
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.databinding.FragmentLoveAlbumsBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.viewmodels.AlbumViewModel
import com.example.singmeapp.viewmodels.LoveAlbumsViewModel
import com.example.singmeapp.viewmodels.MyLibraryViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior


class LoveAlbumsFragment : Fragment(), View.OnClickListener, MenuProvider {

    lateinit var binding: FragmentLoveAlbumsBinding
    lateinit var loveAlbumsViewModel: LoveAlbumsViewModel
    lateinit var albumViewModel: AlbumViewModel

    lateinit var albumAdapter: AlbumAdapter
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var mainActivity: MainActivity
    lateinit var optionsMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        mainActivity = activity as MainActivity
        //setHasOptionsMenu(true)

        val provider = ViewModelProvider(this)
        val albumProvider = ViewModelProvider(this)
        albumViewModel = albumProvider[AlbumViewModel::class.java]
        loveAlbumsViewModel = provider[LoveAlbumsViewModel::class.java]
        albumAdapter = AlbumAdapter(this)
        loveAlbumsViewModel.getAlbums()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentActivity.title = getString(R.string.albums)
        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
        binding = FragmentLoveAlbumsBinding.inflate(layoutInflater)

        binding.rcView.layoutManager = LinearLayoutManager(activity)


        loveAlbumsViewModel.listAlbum.observe(viewLifecycleOwner){
            albumAdapter.initList(it)
            binding.rcView.adapter = albumAdapter
        }

        albumViewModel.isLoveAlbumsListChanged.observe(viewLifecycleOwner){
            binding.loveAlbumsProgressLayout.visibility = View.VISIBLE
            loveAlbumsViewModel.isAlready.value?.put("image", false)
            loveAlbumsViewModel.getAlbums()
        }

        loveAlbumsViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["image"] == true){
                binding.loveAlbumsProgressLayout.visibility = View.GONE
            }
        }

        setButtons()

        return binding.root
    }


    companion object {
        @JvmStatic
        fun newInstance() = LoveAlbumsFragment()
    }

    fun setButtons(){
        binding.tvSortByInLoveAlbums.setOnClickListener(this)
        mainActivity.binding.tvSortByDefault.setOnClickListener(this)
        mainActivity.binding.tvSortByName.setOnClickListener(this)
        mainActivity.binding.tvSortByDate.setOnClickListener(this)
        mainActivity.binding.tvSortByBand.setOnClickListener(this)
    }

    fun doWhenSort(){
        binding.rcView.adapter = albumAdapter
        mainActivity.binding.view15.visibility = View.GONE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.tvSortByInLoveAlbums.id -> {
                mainActivity.binding.sortMenu.visibility = View.VISIBLE
                mainActivity.binding.tvSortByAlbum.visibility = View.GONE
                mainActivity.binding.tvSortByBand.visibility = View.VISIBLE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }

            mainActivity.binding.tvSortByDefault.id -> {
                albumAdapter.sortByFefault()
                doWhenSort()
            }
            mainActivity.binding.tvSortByName.id -> {
                albumAdapter.sortByName()
                doWhenSort()
            }
            mainActivity.binding.tvSortByDate.id -> {
                albumAdapter.sortByDate()
                doWhenSort()
            }
            mainActivity.binding.tvSortByBand.id -> {
                albumAdapter.sortByBand()
                doWhenSort()
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

            if (count == 0) {
                (activity as AppCompatActivity).supportActionBar?.show()
                activity?.onBackPressed()

            } else {
                (activity as AppCompatActivity).supportActionBar?.show()
                findNavController().popBackStack()
            }
        }

        if (menuItem.itemId == R.id.action_search_user){

            val searchView = menuItem.actionView as SearchView
            searchView.queryHint = getString(R.string.type_here_to_search)


            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    albumAdapter.sortBySearch(newText ?: "")
                    binding.rcView.adapter = albumAdapter
                    return true
                }

            })

        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        optionsMenu.clear()
        optionsMenu.close()
    }
}