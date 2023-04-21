package com.example.singmeapp.fragments

import android.os.Bundle
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
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.SortInAdapter
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.databinding.FragmentDiscographyAllBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.DiscographyAllViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior


class DiscographyAllFragment : Fragment(), MenuProvider, OnClickListener {

    lateinit var fragmentActivity: AppCompatActivity
    lateinit var  binding: FragmentDiscographyAllBinding
    lateinit var tracksAdapter: TrackAdapter
    lateinit var albumsAdapter: AlbumAdapter
    lateinit var singlesAdapter: AlbumAdapter
    lateinit var discographyAllViewModel: DiscographyAllViewModel
    lateinit var band: Band
    lateinit var whatIs: String
    lateinit var optionsMenu: Menu
    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity =  activity as AppCompatActivity
        mainActivity = activity as MainActivity
        fragmentActivity.supportActionBar?.show()
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)


        band = arguments?.getSerializable("band") as Band
        val provider = ViewModelProvider(this)
        discographyAllViewModel = provider[DiscographyAllViewModel::class.java]
        discographyAllViewModel.getMembers(band)
        whatIs = arguments?.getString("whatIs").toString()
        when(whatIs){
            "tracks" -> {
                fragmentActivity.title = getString(R.string.tracks)
                discographyAllViewModel.getTracks(band)
                tracksAdapter = TrackAdapter(activity as AppCompatActivity, this)
            }
            "albums" -> {
                fragmentActivity.title = getString(R.string.albums)
                discographyAllViewModel.getAlbums(band)
                albumsAdapter = AlbumAdapter(this)
            }
            "singles" -> {
                fragmentActivity.title = getString(R.string.singles_ep)
                discographyAllViewModel.getSingles(band)
                singlesAdapter = AlbumAdapter(this)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscographyAllBinding.inflate(layoutInflater)
        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
        binding.rcDiscographyAll.layoutManager = LinearLayoutManager(context)

        setButtons()

        when(whatIs){
            "tracks" -> {
                discographyAllViewModel.listTrack.observe(viewLifecycleOwner){
                    tracksAdapter.initList(it)
                    binding.rcDiscographyAll.adapter = tracksAdapter
                }

                discographyAllViewModel.isAlready.observe(viewLifecycleOwner){
                    if (it["track"] == true && it["trackImage"] == true){
                        binding.discographyAllProgressLayout.visibility = View.GONE
                    }
                }
            }
            "albums" -> {
                discographyAllViewModel.listAlbum.observe(viewLifecycleOwner){
                    albumsAdapter.initList(it)
                    binding.rcDiscographyAll.adapter = albumsAdapter
                }

                discographyAllViewModel.isAlready.observe(viewLifecycleOwner){
                    if (it["albumImage"] == true){
                        binding.discographyAllProgressLayout.visibility = View.GONE
                    }
                }
            }
            "singles" -> {
                discographyAllViewModel.listSingle.observe(viewLifecycleOwner){
                    singlesAdapter.initList(it)
                    binding.rcDiscographyAll.adapter = singlesAdapter
                }

                discographyAllViewModel.isAlready.observe(viewLifecycleOwner){
                    if (it["singleImage"] == true){
                        binding.discographyAllProgressLayout.visibility = View.GONE
                    }
                }
            }
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = DiscographyAllFragment()
    }

    fun setButtons(){
        binding.tvSortByInDiscographyAll.setOnClickListener(this@DiscographyAllFragment)
        mainActivity.binding.tvSortByDefault.setOnClickListener(this@DiscographyAllFragment)
        mainActivity.binding.tvSortByName.setOnClickListener(this@DiscographyAllFragment)
        mainActivity.binding.tvSortByDate.setOnClickListener(this@DiscographyAllFragment)
        mainActivity.binding.tvSortByAlbum.setOnClickListener(this@DiscographyAllFragment)
        mainActivity.binding.tvSortByAlbum.setOnClickListener(this@DiscographyAllFragment)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                val count: Int? = activity?.supportFragmentManager?.backStackEntryCount
                if (count == 0) {
                    activity?.onBackPressed()
                } else {
                    findNavController().popBackStack()
                }
                return true
            }

            R.id.action_search_user -> {
                val searchView = menuItem.actionView as SearchView
                searchView.queryHint = getString(R.string.type_here_to_search)


                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        returnAdapter().sortBySearch(newText ?: "")
                        initAdapter()
                        return true
                    }

                })
                return true
            }
        }
        return false
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.tvSortByInDiscographyAll.id -> {
                when (whatIs){
                    "tracks" -> {
                        mainActivity.binding.tvSortByBand.visibility = View.VISIBLE
                        mainActivity.binding.tvSortByAlbum.visibility = View.VISIBLE
                    }
                    "albums" -> {
                        mainActivity.binding.tvSortByBand.visibility = View.VISIBLE
                        mainActivity.binding.tvSortByAlbum.visibility = View.GONE
                    }
                    "singles" -> {
                        mainActivity.binding.tvSortByBand.visibility = View.VISIBLE
                        mainActivity.binding.tvSortByAlbum.visibility = View.GONE
                    }
                }
                mainActivity.binding.sortMenu.visibility = View.VISIBLE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }
            mainActivity.binding.tvSortByDefault.id -> {
                returnAdapter().sortByDefault()
                doWhenSort()
            }
            mainActivity.binding.tvSortByName.id -> {
                returnAdapter().sortByName()
                doWhenSort()
            }
            mainActivity.binding.tvSortByDate.id -> {
                returnAdapter().sortByDate()
                doWhenSort()
            }
            mainActivity.binding.tvSortByAlbum.id -> {
                returnAdapter().sortByAlbum()
                doWhenSort()
            }
            mainActivity.binding.tvSortByBand.id -> {
                returnAdapter().sortByBand()
                doWhenSort()
            }
        }
    }

    fun doWhenSort(){
        initAdapter()
        mainActivity.binding.view15.visibility = View.GONE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun initAdapter(){
        when (whatIs){
            "tracks" -> binding.rcDiscographyAll.adapter = tracksAdapter
            "albums" ->  binding.rcDiscographyAll.adapter = albumsAdapter
            "singles" -> binding.rcDiscographyAll.adapter = singlesAdapter
        }
    }

    fun returnAdapter(): SortInAdapter{
        when (whatIs){
            "tracks" -> return tracksAdapter
            "albums" -> return  albumsAdapter
            "singles" -> return singlesAdapter
        }

        return tracksAdapter


    }

    override fun onDestroyView() {
        super.onDestroyView()
        optionsMenu.clear()
        optionsMenu.close()
    }
}