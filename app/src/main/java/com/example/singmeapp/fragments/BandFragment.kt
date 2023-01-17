package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentBandBinding

class BandFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentBandBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            wrapItem.layoutParams = params
        }
        else {
            var params = wrapItem.layoutParams
            params.height = convert(20)
            wrapItem.layoutParams = params
        }
    }
}