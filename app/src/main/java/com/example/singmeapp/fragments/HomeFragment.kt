package com.example.singmeapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.singmeapp.R
import com.example.singmeapp.adapters.PostNewsAdapter
import com.example.singmeapp.databinding.FragmentHomeBinding
import com.example.singmeapp.items.Post
import com.example.singmeapp.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var fragmentActivity: AppCompatActivity
    lateinit var postNewsAdapter: PostNewsAdapter
    lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentActivity = activity as AppCompatActivity
        fragmentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        postNewsAdapter = PostNewsAdapter(this)
        val provider = ViewModelProvider(this)
        homeViewModel = provider[HomeViewModel::class.java]
        homeViewModel.getPosts()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity.title = getString(R.string.home)
        fragmentActivity.titleColor = R.color.white
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.rvPosts.layoutManager = LinearLayoutManager(context)

        homeViewModel.listPosts.observe(viewLifecycleOwner){
            if (it.isNotEmpty()) {
                binding.textView16.visibility = View.GONE
                binding.rvPosts.visibility = View.VISIBLE
                postNewsAdapter.list.clear()
                postNewsAdapter.list.addAll(it as ArrayList<Post>)
                binding.rvPosts.adapter = postNewsAdapter
            } else {
                binding.rvPosts.visibility = View.GONE
                binding.textView16.visibility = View.VISIBLE
            }
        }

        homeViewModel.isAlready.observe(viewLifecycleOwner){
            if (it["bandAvatar"] == true && it["albumCover"] == true){
                binding.homeProgressLayout.visibility = View.GONE
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}