package com.example.singmeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.singmeapp.databinding.ActivityMainBinding
import com.example.singmeapp.fragments.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    public lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.libraryItem -> {supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, LibraryFragment.newInstance()).commit()}
                R.id.profileItem -> {
                    if (auth.currentUser != null)
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, ProfileFragment.newInstance()).commit()
                    else supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, NotAuthorizedFragment.newInstance()).commit()
                }
                R.id.homeItem -> {supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, HomeFragment.newInstance()).commit()}
                R.id.messengerItem -> {supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, MessengerFragment.newInstance()).commit()}
            }
            true
        }
    }


}