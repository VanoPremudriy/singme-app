package com.example.singmeapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.singmeapp.fragments.CatalogueNewsFragment

class CataloguePagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment){
    override fun getItemCount(): Int {
        return 1
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CatalogueNewsFragment.newInstance()
            else -> CatalogueNewsFragment.newInstance()
        }
    }

}