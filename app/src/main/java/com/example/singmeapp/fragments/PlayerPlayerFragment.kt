package com.example.singmeapp.fragments

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.example.singmeapp.R
import com.example.singmeapp.databinding.FragmentPlayerBinding
import com.example.singmeapp.databinding.FragmentPlayerPlayerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.function.Consumer
import java.io.IOException

class PlayerPlayerFragment : Fragment() {

    lateinit var storage: FirebaseStorage
    var mPlayer: MediaPlayer = MediaPlayer()
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    private var database = FirebaseDatabase.getInstance()
    private val firebaseUrl = "https://firebasestorage.googleapis.com/v0/b/singmedb.appspot.com/o/songs"
    private val token = "?alt=media&token=0cae4f78-6eb5-4026-b4ed-49cb0f844f86"
    val songList = ArrayList<String>()
    val coverList = ArrayList<String>()


    lateinit var binding: FragmentPlayerPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerPlayerBinding.inflate(layoutInflater)
        storage = FirebaseStorage.getInstance()


        /*database.getReference("songs").addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach(Consumer { t ->
                    songList.add("/" + t.value.toString())
                    Log.e("SONGS", t.value.toString())
                })
                mPlayer.setDataSource(firebaseUrl + songList[0].replace("/", "%2F") + token)
                mPlayer.prepare()
                initializeSeekBar()
                initializeButtonsClickListeners()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )*/

        database.getReference("songs/user_songs/koiLcWJxjnfOkSiXIl93jA449T22/love").addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("SONGS", snapshot.value.toString())
                snapshot.children.forEach(Consumer { t ->
                    songList.add("/${t.child("name").value.toString()}.mp3")gi
                    Log.e("SONGS", t.child("name").value.toString())
                    Log.e("SONGS", t.child("Genres").value.toString())
                    //database.getReference("songs/user_songs/koiLcWJxjnfOkSiXIl93jA449T22/love/"+t.key.toString()).child("Genres").child("0").setValue("Rock")
                    //database.getReference("songs/user_songs/koiLcWJxjnfOkSiXIl93jA449T22/love/"+t.key.toString()).child("Genres").child("1").setValue("J-pop")
                    //database.getReference("songs/user_songs/koiLcWJxjnfOkSiXIl93jA449T22/love/"+t.key.toString()).child("Genres").child("1").setValue("Pizdec")

                })
                mPlayer.setDataSource(firebaseUrl + songList[0].replace("/", "%2F") + token)
                mPlayer.prepare()
                initializeSeekBar()
                initializeButtonsClickListeners()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )

        database.getReference("covers").addValueEventListener(object : ValueEventListener {
            @SuppressLint("RestrictedApi")
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach(Consumer { t ->
                    coverList.add(t.value.toString())
                    Log.e("COVERS", t.value.toString())
                })
                initializeCover(coverList[0])
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )


        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
                if(b) {
                    mPlayer.seekTo(i * 1000)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })


        // Inflate the layout for this fragment
        return binding.root
    }

    fun initializeButtonsClickListeners(){
        binding.ibPlay.setOnClickListener {

            if (!mPlayer.isPlaying) {
                binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
                mPlayer.start()
            } else {
                binding.ibPlay.setImageResource(android.R.drawable.ic_media_play)
                mPlayer.pause()
            }

        }

        binding.ibMusicRight.setOnClickListener {

            mPlayer.stop()
            mPlayer = MediaPlayer()
            mPlayer.setDataSource(firebaseUrl + songList[1].replace("/", "%2F") + token)
            mPlayer.prepare()
            initializeSeekBar()
            initializeCover(coverList[1])
            binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
            mPlayer.start()

        }

        binding.ibMusicLeft.setOnClickListener {

            mPlayer.stop()
            mPlayer = MediaPlayer()
            mPlayer.setDataSource(firebaseUrl + songList[0].replace("/", "%2F") + token)
            mPlayer.prepare()
            initializeSeekBar()
            initializeCover(coverList[0])
            binding.ibPlay.setImageResource(android.R.drawable.ic_media_pause)
            mPlayer.start()

        }


    }

    fun initializeCover(coverName: String){
        try {
            val localFile: File = File.createTempFile("default_picture", "jpg")
            storage.getReferenceFromUrl("gs://singmedb.appspot.com/covers").child(coverName).getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    binding.imageView6.setImageBitmap(bitmap)
                }.addOnFailureListener { }
        } catch (e: IOException) {
        }
    }

    fun initializeSeekBar() {
        binding.seekBar.max = mPlayer.seconds

        runnable = Runnable {
            binding.seekBar.progress = mPlayer.currentSeconds
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    // Creating an extension property to get the media player time duration in seconds
    val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }
    // Creating an extension property to get media player current position in seconds
    val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }

    companion object {
        @JvmStatic
        fun newInstance() = PlayerPlayerFragment()

    }

}