package com.example.singmeapp.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.BandItemBinding
import com.example.singmeapp.fragments.MyLibraryFragment
import com.example.singmeapp.items.Album
import com.example.singmeapp.items.Band
import com.example.singmeapp.viewmodels.AlbumViewModel
import com.example.singmeapp.viewmodels.BandViewModel
import com.squareup.picasso.Picasso

class BandAdapter(val fragment: Fragment): RecyclerView.Adapter<BandAdapter.BandHolder>(), SortInAdapter{

    var bandList = ArrayList<Band>()
    var bandListDefaultCopy = ArrayList<Band>()


    class BandHolder(item: View, val fragment: Fragment): RecyclerView.ViewHolder(item){
        lateinit var bandViewModel: BandViewModel

        val binding =  BandItemBinding.bind(item)

        fun bind(band: Band)=with(binding){
            if (band.imageUrl != ""){
                Picasso.get().load(band.imageUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivItemBandAvatar)
            }

            tvItemBandName.text = band.name

            val provider = ViewModelProvider(fragment)
            bandViewModel = provider[BandViewModel::class.java]
            BandItemLayout.setOnClickListener{
                if (band.imageUrl != "") {
                    val bundle = Bundle()
                    bundle.putInt("Back", R.id.loveBandsFragment)
                    bundle.putSerializable("bandUuid", band.uuid)
                    NavHostFragment.findNavController(fragment).navigate(R.id.bandFragment, bundle)
                }
                else Toast.makeText(fragment.context, "Загрузка", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BandHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.band_item, parent, false)
        return BandHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: BandHolder, position: Int) {
        holder.bind(bandList[position])
    }

    override fun getItemCount(): Int {
        return bandList.size
    }

    fun initList(bands: List<Band>) {
        bandList.clear()
        bandListDefaultCopy.clear()
        bandList.addAll(bands)
        bandListDefaultCopy.addAll(bands)
    }

    override fun sortByDefault(){
        bandList.clear()
        bandList.addAll(bandListDefaultCopy)
    }

    override fun sortByName() = bandList.sortBy { band: Band ->  band.name}
    override fun sortByDate() =bandList.sortBy { band: Band ->  band.date}
    override fun sortByBand() {
        TODO("Not yet implemented")
    }

    override fun sortByAlbum() {
        TODO("Not yet implemented")
    }

    override fun sortBySearch(search: String){
        bandList.clear()
        bandList.addAll(bandListDefaultCopy.filter { band: Band -> band.name.lowercase().contains(search.lowercase()) })
    }

}