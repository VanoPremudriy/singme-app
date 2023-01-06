package com.example.singmeapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.singmeapp.items.Song

class PlayerPlaylistViewModel : ViewModel(){
    var songList = MutableLiveData<List<Song>>()
    var url = MutableLiveData<String>()


}