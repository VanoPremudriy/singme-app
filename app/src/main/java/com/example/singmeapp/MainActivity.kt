package com.example.singmeapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.singmeapp.adapters.PlayerPagerAdapter
import com.example.singmeapp.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    lateinit var auth: FirebaseAuth

    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var bottomSheetBehavior2: BottomSheetBehavior<View>

    lateinit var viewPager: ViewPager2
    lateinit var  tabLayout: TabLayout
    lateinit var notificationManagerCompat: NotificationManagerCompat

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(){
        val channel = NotificationChannel(CreateNotification().CHANNEL_ID, "KOD DEV", NotificationManager.IMPORTANCE_LOW)
        notificationManagerCompat = this.let { NotificationManagerCompat.from(it) }!!
        notificationManagerCompat.createNotificationChannel(channel)
    }

    val broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.getStringExtra("actionname")){
                CreateNotification().ACTION_PLAY -> {
                    Log.e("ACTION", "PLAY")
                }
                CreateNotification().ACTION_NEXT -> {
                    Log.e("ACTION", "NEXT")
                }
                CreateNotification().ACTION_PREVIOUS -> {
                    Log.e("ACTION", "PREVIOUS")
                }
            }
        }

    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun verifyStoragePermissions() {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )


        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            if (this != null) {
                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyStoragePermissions()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        createChannel()
        /*registerReceiver(broadcastReceiver, IntentFilter("TRACKSTRACKS"))
        startService(Intent(baseContext, OnClearFromRecentService::class.java))*/


        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController
        //navController.setGraph(R.navigation.main_nav)

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
                        binding.playerTrackMenu.visibility = View.GONE
                        binding.AddPlaylistMenu.visibility = View.GONE
                        binding.playlistMenu.visibility = View.GONE
                        binding.profileMenu.visibility = View.GONE
                        binding.sortMenu.visibility = View.GONE
                        binding.userSortMenu.visibility = View.GONE
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
                        binding.playerTrackMenu.visibility = View.GONE
                        binding.AddPlaylistMenu.visibility = View.GONE
                        binding.playlistMenu.visibility = View.GONE
                        binding.profileMenu.visibility = View.GONE
                        binding.sortMenu.visibility = View.GONE
                        binding.userSortMenu.visibility = View.GONE
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
        if (auth.currentUser != null)
            //navController.navigate(R.id.action_global_homeFragment)
        else navController.navigate(R.id.action_global_notAuthorizedFragment, null, NavOptions.Builder().setPopUpTo(R.id.homeFragment,true).build())

        binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.myLibraryFragment -> {
                    if (auth.currentUser != null)
                        navController.navigate(R.id.action_global_myLibraryFragment)
                    else navController.navigate(R.id.action_global_notAuthorizedFragment)
                }
                R.id.profileFragment -> {
                    if (auth.currentUser != null)
                        navController.navigate(R.id.action_global_profileFragment)
                    else navController.navigate(R.id.action_global_notAuthorizedFragment)
                }
                R.id.homeFragment -> {
                    if (auth.currentUser != null)
                        navController.navigate(R.id.action_global_homeFragment)
                    else navController.navigate(R.id.action_global_notAuthorizedFragment)
                }
                R.id.messengerFragment -> {
                    if (auth.currentUser != null)
                        navController.navigate(R.id.action_global_messengerFragment)
                    else navController.navigate(R.id.action_global_notAuthorizedFragment)
                }
                R.id.catalogueFragment -> {
                    if (auth.currentUser != null)
                        navController.navigate(R.id.catalogueFragment)
                    else navController.navigate(R.id.action_global_notAuthorizedFragment)
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (auth.currentUser?.uid == null){
            navController.navigate(R.id.notAuthorizedFragment)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        notificationManagerCompat.cancelAll()
    }

}