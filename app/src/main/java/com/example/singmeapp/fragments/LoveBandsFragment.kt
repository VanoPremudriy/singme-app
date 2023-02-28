package com.example.singmeapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.AlbumAdapter
import com.example.singmeapp.adapters.BandAdapter
import com.example.singmeapp.databinding.FragmentLoveBandsBinding
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.User
import com.example.singmeapp.viewmodels.LoveAlbumsViewModel
import com.example.singmeapp.viewmodels.LoveBandsViewModel
import java.util.zip.Inflater


class LoveBandsFragment : Fragment(), MenuProvider {
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var binding: FragmentLoveBandsBinding
    lateinit var loveBandsViewModel: LoveBandsViewModel
    lateinit var bandAdapter: BandAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity


        setHasOptionsMenu(true)

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
            fragmentActivity.addMenuProvider(this, viewLifecycleOwner)
        }
        else fragmentActivity.title = getString(R.string.Bands)

        binding = FragmentLoveBandsBinding.inflate(layoutInflater)

        binding.rcView.layoutManager = GridLayoutManager(context, 3)
        bandAdapter = BandAdapter(this)
        loveBandsViewModel.listBand.observe(viewLifecycleOwner){
            bandAdapter.bandList.clear()
            bandAdapter.bandList.addAll(it as ArrayList<Band>) /* = java.util.ArrayList<com.example.singmeapp.items.Album> */
            binding.rcView.adapter = bandAdapter
        }

        loveBandsViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["back"] == true && it["image"] == true){
                binding.loveBandsProgressLayout.visibility = View.GONE
            }
        }
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            val count: Int? = activity?.supportFragmentManager?.backStackEntryCount

            if (count == 0) {
                activity?.onBackPressed()
            } else {
                findNavController().popBackStack()
            }
        }

        if (item.itemId == R.id.add_band){
            findNavController().navigate(R.id.createBandFragment)
        }
        return true
    }

    companion object {

        @JvmStatic
        fun newInstance() = LoveBandsFragment()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        if (arguments?.getSerializable("curUser") != null){
          menuInflater.inflate(R.menu.user_bands_menu, menu)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.add_band -> {
                Toast.makeText(context, "wsrfgh", Toast.LENGTH_SHORT).show()
                    //findNavController().navigate(R.id.createBandFragment)
                    return true
            }
        }
        if (menuItem.itemId == R.id.add_band){
            findNavController().navigate(R.id.createBandFragment)
            return true
        }
        return false
    }
}