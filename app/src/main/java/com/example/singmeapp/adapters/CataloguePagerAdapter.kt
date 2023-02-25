package com.example.singmeapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.singmeapp.fragments.CatalogueNewsFragment
import com.example.singmeapp.fragments.CataloguePopularFragment

class CataloguePagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CatalogueNewsFragment.newInstance()
            1 -> CataloguePopularFragment.newInstance()
            else -> CatalogueNewsFragment.newInstance()
        }
    }

}