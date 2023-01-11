package com.example.singmeapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.items.Track

class PlayerPlaylistViewModel : ViewModel(){
    var trackList = MutableLiveData<List<Track>>()
    var url = MutableLiveData<String>()
    var currentTrackId = MutableLiveData<Int>()
    var isPlaying = MutableLiveData<Boolean>()


}