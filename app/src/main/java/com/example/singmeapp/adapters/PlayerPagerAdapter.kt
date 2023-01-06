package com.example.singmeapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.singmeapp.fragments.PlayerPlayerFragment
import com.example.singmeapp.fragments.PlayerPlaylistFragment

class PlayerPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> PlayerPlayerFragment.newInstance()
            1 -> PlayerPlaylistFragment.newInstance()
            else -> PlayerPlayerFragment()//{throw Resources.NotFoundException("PositionNotFound")}
        }
    }
}