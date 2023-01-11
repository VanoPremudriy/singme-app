package com.example.singmeapp

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.singmeapp.adapters.PlayerPagerAdapter
import com.example.singmeapp.databinding.ActivityMainBinding
import com.example.singmeapp.viewmodels.PlayerPlaylistViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    lateinit var auth: FirebaseAuth

    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var viewPager: ViewPager2
    lateinit var  tabLayout: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        super.onPostResume()


        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.main_nav)

        setNavigation()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.player.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED ->{
                        supportActionBar?.hide()
                        binding.bNav.visibility = View.GONE
                        bottomSheet.setClickable(true)
                        binding.player.ibPlayUpMenu.visibility = View.GONE
                        binding.player.textUpMenu.visibility = View.GONE
                        binding.player.ibClose.visibility = View.GONE
                        binding.player.ibDown.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        supportActionBar?.show()
                        binding.bNav.visibility = View.VISIBLE
                        bottomSheet.setClickable(false)
                        binding.player.ibPlayUpMenu.visibility = View.VISIBLE
                        binding.player.textUpMenu.visibility = View.VISIBLE
                        binding.player.ibClose.visibility = View.VISIBLE
                        binding.player.ibDown.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_HIDDEN ->{
                        supportActionBar?.show()
                        binding.bNav.visibility = View.VISIBLE
                        bottomSheet.setClickable(false)
                    }

                    else -> {

                    }
                }
            }

        })

        viewPager = binding.player.viewPager
        tabLayout = binding.player.tabLayout


        viewPager.adapter =  PlayerPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, index ->
            tab.icon = when(index){
                0 ->  getDrawable(android.R.drawable.radiobutton_off_background)
                1 ->  getDrawable(android.R.drawable.ic_menu_sort_by_size)
                else -> {throw Resources.NotFoundException("14")}
            }
        }.attach()

    }

    fun setNavigation(){
        binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.libraryFragment ->  {navController.navigate(R.id.myLibraryFragment)}//{navController.navigate(R.id.action_global_libraryFragment) }
                R.id.profileFragment -> {
                    if (auth.currentUser != null)
                        navController.navigate(R.id.action_global_profileFragment)
                    else navController.navigate(R.id.action_global_notAuthorizedFragment)
                }
                R.id.homeFragment -> {navController.navigate(R.id.action_global_homeFragment)}
                R.id.messengerFragment -> navController.navigate(R.id.action_global_messengerFragment)
            }
            true
        }
    }




}