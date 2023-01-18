package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.MemberAdapter
import com.example.singmeapp.databinding.FragmentBandBinding
import com.example.singmeapp.items.Band
import com.example.singmeapp.items.Member
import com.example.singmeapp.viewmodels.BandViewModel
import com.squareup.picasso.Picasso

class BandFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentBandBinding
    lateinit var memberAdapter: MemberAdapter
    lateinit var bandViewModel: BandViewModel
    lateinit var band: Band
    lateinit var fragmentActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

    }
    fun convert(value: Int):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBandBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        setButtons()
        band = arguments?.getSerializable("band") as Band
        fragmentActivity.title = band.name
        binding.tvBandNameBandFragment.text = band.name
        if (band.imageUrl != ""){
            Picasso.get().load(band.imageUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivBandAvatar)
        }
        val provider = ViewModelProvider(this)
        bandViewModel = provider[BandViewModel::class.java]
        bandViewModel.getMembers(band)
        binding.rcVievMembers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        memberAdapter = MemberAdapter()
        bandViewModel.listMember.observe(viewLifecycleOwner){
            memberAdapter.memberList =it as ArrayList<Member> /* = java.util.ArrayList<com.example.singmeapp.items.Member> */
            binding.rcVievMembers.adapter = memberAdapter
        }
        return binding.root
    }

    fun setButtons(){
        binding.ibWrapBandInfo.setOnClickListener(this@BandFragment)
        binding.ibWrapBandMembers.setOnClickListener(this@BandFragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BandFragment()
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            binding.ibWrapBandInfo.id -> {
                wrap(binding.llBandInfo)
            }

            binding.ibWrapBandMembers.id -> {
                wrap(binding.llBandMembers)
            }

            binding.ibAddBand.id -> {
                Toast.makeText(context, "Band added", Toast.LENGTH_SHORT).show()
            }

            binding.ibSubscribeBand.id ->{
                Toast.makeText(context, "Subscribed", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun wrap(wrapItem: View){
        if (wrapItem.height == convert(20)) {
            Log.e("efs", "YES")
            var params = wrapItem.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.rcVievMembers.adapter = memberAdapter
            wrapItem.layoutParams = params
        }
        else {
            var params = wrapItem.layoutParams
            params.height = convert(20)
            wrapItem.layoutParams = params
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            arguments?.let { findNavController().navigate(it.getInt("Back")) }
        }
        return true
    }
}