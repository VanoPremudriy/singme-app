package com.example.singmeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.TrackItemBinding
import com.example.singmeapp.items.Track
import com.example.singmeapp.viewmodels.ChooseTrackForPlaylistViewModel
import com.squareup.picasso.Picasso

class ChooseTrackForPlaylistAdapter(val fragment: Fragment): RecyclerView.Adapter<ChooseTrackForPlaylistAdapter.ChooseTrackForPlaylistHolder>() {
    var trackList = ArrayList<Track>()

    class ChooseTrackForPlaylistHolder(item: View, val fragment: Fragment): RecyclerView.ViewHolder(item){
        val binding = TrackItemBinding.bind(item)
        val activity = fragment.activity
        val provider = ViewModelProvider(fragment)
        val chooseTrackForPlaylistViewModel = provider[ChooseTrackForPlaylistViewModel::class.java]
        fun bind(track: Track){
            binding.ibItemTrackMenu.visibility = View.GONE
            binding.tvItemTrackName.text = track.name
            binding.tvItemTrackBandName.text = track.band
            if (track.imageUrl != ""){
                    Picasso.get().load(track.imageUrl).fit().into(binding.ivItemTrackCover)
            }
            binding.SongLayout.setOnClickListener{
                chooseTrackForPlaylistViewModel.setTrackToPlaylist(track.uuid)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseTrackForPlaylistHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return ChooseTrackForPlaylistHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: ChooseTrackForPlaylistHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}