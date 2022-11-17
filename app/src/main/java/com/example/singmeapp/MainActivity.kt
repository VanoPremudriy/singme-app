package com.example.singmeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.singmeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.libraryItem -> {supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, LibraryFragment.newInstance()).commit()}
                R.id.profileItem -> {supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, ProfileFragment.newInstance()).commit()}
                R.id.homeItem -> {supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, HomeFragment.newInstance()).commit()}
                R.id.messengerItem -> {supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, MessengerFragment.newInstance()).commit()}
            }
            true
        }
    }


}