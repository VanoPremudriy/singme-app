package com.example.singmeapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.singmeapp.databinding.ActivityMainBinding
import com.example.singmeapp.fragments.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import java.time.Duration

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    lateinit var auth: FirebaseAuth

    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>



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

        binding.player.ibClose.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

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