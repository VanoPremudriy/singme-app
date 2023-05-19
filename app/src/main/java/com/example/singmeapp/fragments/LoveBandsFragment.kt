package com.example.singmeapp.fragments

import android.os.Bundle
import android.view.*
import android.view.View.OnClickListener
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.MainActivity
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.BandAdapter
import com.example.singmeapp.databinding.FragmentLoveBandsBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.User
import com.example.singmeapp.viewmodels.LoveAlbumsViewModel
import com.example.singmeapp.viewmodels.LoveBandsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.zip.Inflater


class LoveBandsFragment : Fragment(), MenuProvider, OnClickListener {
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentLoveBandsBinding
    lateinit var loveBandsViewModel: LoveBandsViewModel
    lateinit var bandAdapter: BandAdapter
    lateinit var mainActivity: MainActivity
    lateinit var optionsMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        mainActivity = activity as MainActivity

        //setHasOptionsMenu(true)

        val provider = ViewModelProvider(this)
        loveBandsViewModel= provider[LoveBandsViewModel::class.java]
        if (arguments?.getSerializable("curUser") != null){
            loveBandsViewModel.getBands(requireArguments().getSerializable("curUser") as User)
        }
        else loveBandsViewModel.getBands()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (arguments?.getSerializable("curUser") != null){
            fragmentActivity.title  = getString(R.string.projects)
        }
        else fragmentActivity.title = getString(R.string.Bands)

        fragmentActivity.addMenuProvider(this, viewLifecycleOwner)

        binding = FragmentLoveBandsBinding.inflate(layoutInflater)

        binding.rcView.layoutManager = GridLayoutManager(context, 3)
        bandAdapter = BandAdapter(this)

        observes()
        setButtons()

        return binding.root
    }

    fun observes(){
        loveBandsViewModel.listBand.observe(viewLifecycleOwner){
            bandAdapter.initList(it)
            binding.rcView.adapter = bandAdapter
        }

        loveBandsViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["back"] == true && it["image"] == true){
                binding.loveBandsProgressLayout.visibility = View.GONE
            }
        }
    }

    fun setButtons(){
        binding.tvSortByInLoveBands.setOnClickListener(this)
        mainActivity.binding.tvSortByDefault.setOnClickListener(this)
        mainActivity.binding.tvSortByName.setOnClickListener(this)
        mainActivity.binding.tvSortByDate.setOnClickListener(this)
    }



    companion object {

        @JvmStatic
        fun newInstance() = LoveBandsFragment()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        if (arguments?.getSerializable("curUser") != null && (arguments?.getSerializable("curUser") as User).uuid == loveBandsViewModel.auth.currentUser?.uid.toString()){
          menuInflater.inflate(R.menu.user_bands_menu, menu)
        } else {
            menuInflater.inflate(R.menu.search_menu, menu)
        }
        optionsMenu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.add_band){
            findNavController().navigate(R.id.createBandFragment)
            return true
        }

        if (menuItem.itemId == android.R.id.home){
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

            if (count == 0) {
                activity?.onBackPressed()
            } else {
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
                    bandAdapter.sortBySearch(newText ?: "")
                    binding.rcView.adapter = bandAdapter
                    return true
                }

            })

        }

        if (menuItem.itemId == R.id.action_search_bands){
            val searchView = menuItem.actionView as SearchView
            searchView.queryHint = getString(R.string.type_here_to_search)


            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    bandAdapter.sortBySearch(newText ?: "")
                    binding.rcView.adapter = bandAdapter
                    return true
                }

            })
        }
        return false
    }

    fun doWhenSort(){
        binding.rcView.adapter  = bandAdapter
        mainActivity.binding.view15.visibility = View.GONE
        mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            binding.tvSortByInLoveBands.id -> {
                mainActivity.binding.sortMenu.visibility = View.VISIBLE
                mainActivity.binding.tvSortByAlbum.visibility = View.GONE
                mainActivity.binding.tvSortByBand.visibility = View.GONE
                mainActivity.binding.view15.visibility = View.VISIBLE
                mainActivity.bottomSheetBehavior2.state = BottomSheetBehavior.STATE_EXPANDED
            }

            mainActivity.binding.tvSortByDefault.id -> {
                bandAdapter.sortByDefault()
                doWhenSort()
            }
            mainActivity.binding.tvSortByName.id -> {
                bandAdapter.sortByName()
                doWhenSort()
            }

            mainActivity.binding.tvSortByDate.id -> {
                bandAdapter.sortByDate()
                doWhenSort()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        optionsMenu.clear()
        optionsMenu.close()
    }
}