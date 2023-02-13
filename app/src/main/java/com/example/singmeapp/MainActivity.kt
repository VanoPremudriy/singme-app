package com.example.singmeapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.findFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.singmeapp.adapters.PlayerPagerAdapter
import com.example.singmeapp.databinding.ActivityMainBinding
import com.example.singmeapp.fragments.AlbumFragment
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
    lateinit var bottomSheetBehavior2: BottomSheetBehavior<View>

    lateinit var viewPager: ViewPager2
    lateinit var  tabLayout: TabLayout


    @SuppressLint("UseCompatLoadingForDrawables")
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

        bottomSheetBehavior2 = BottomSheetBehavior.from(binding.bottomSheetMenu)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.player.root)
        bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
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
                        if (navController.currentDestination?.id?.equals(R.id.albumFragment) == false)
                        supportActionBar?.show()
                        binding.bNav.visibility = View.VISIBLE
                        bottomSheet.setClickable(false)
                        binding.player.ibPlayUpMenu.visibility = View.VISIBLE
                        binding.player.textUpMenu.visibility = View.VISIBLE
                        binding.player.ibClose.visibility = View.VISIBLE
                        binding.player.ibDown.visibility = View.GONE
                    }

                    BottomSheetBehavior.STATE_HIDDEN ->{
                        if (navController.currentDestination?.id?.equals(R.id.albumFragment) == false
                            && navController.currentDestination?.id?.equals(R.id.chatFragment) == false)
                        supportActionBar?.show()
                        binding.bNav.visibility = View.VISIBLE
                        bottomSheet.setClickable(false)
                    }

                    else -> {

                    }
                }
            }

        })

        bottomSheetBehavior2.addBottomSheetCallback(object :BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.view15.visibility = View.GONE
                        binding.friendMenu.visibility = View.GONE
                        binding.requestMenu.visibility = View.GONE
                        binding.myRequestMenu.visibility = View.GONE
                        binding.meMenu.visibility = View.GONE
                        binding.unknownUserMenu.visibility = View.GONE
                        binding.inAlbumMenu.visibility = View.GONE
                        binding.trackMenu.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.view15.visibility = View.GONE
                        binding.friendMenu.visibility = View.GONE
                        binding.requestMenu.visibility = View.GONE
                        binding.myRequestMenu.visibility = View.GONE
                        binding.meMenu.visibility = View.GONE
                        binding.unknownUserMenu.visibility = View.GONE
                        binding.inAlbumMenu.visibility = View.GONE
                        binding.trackMenu.visibility = View.GONE
                    }
            }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

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

    override fun onResume() {
        super.onResume()
        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun setNavigation(){
        binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.myLibraryFragment ->  {navController.navigate(R.id.action_global_myLibraryFragment)}//{navController.navigate(R.id.action_global_libraryFragment) }
                R.id.profileFragment -> {
                    if (auth.currentUser != null)
                        navController.navigate(R.id.action_global_profileFragment)
                    else navController.navigate(R.id.action_global_notAuthorizedFragment)
                }
                R.id.homeFragment -> {navController.navigate(R.id.action_global_homeFragment)}
                R.id.messengerFragment -> navController.navigate(R.id.action_global_messengerFragment)
                R.id.catalogueFragment -> navController.navigate(R.id.createAlbumFragment)
            }
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (navController.currentDestination?.id != R.id.albumFragment){
            supportActionBar?.show()
        }
        if (navController.currentDestination?.id != R.id.chatFragment ){
            binding.bNav.visibility = View.VISIBLE
        }
        if (binding.player.root.visibility == View.INVISIBLE){
            binding.player.root.visibility = View.VISIBLE
        }
        when(navController.currentDestination?.id){
            R.id.homeFragment ->{
                binding.bNav.menu.getItem(0).isChecked = true
            }
            R.id.catalogueFragment ->{
                binding.bNav.menu.getItem(1).isChecked = true
            }
            R.id.messengerFragment ->{
                binding.bNav.menu.getItem(2).isChecked = true
            }
            R.id.myLibraryFragment ->{
                binding.bNav.menu.getItem(3).isChecked = true
            }
            R.id.profileFragment ->{
                binding.bNav.menu.getItem(4).isChecked = true
            }
        }

    }




}