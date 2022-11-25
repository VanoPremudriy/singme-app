package com.example.singmeapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.singmeapp.databinding.ActivityMainBinding
import com.example.singmeapp.fragments.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        super.onPostResume()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.main_nav)

       binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.libraryFragment ->  {navController.navigate(R.id.playerFragment)}//{navController.navigate(R.id.action_global_libraryFragment) }
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

    override fun onResume() {
        super.onResume()
    }


}