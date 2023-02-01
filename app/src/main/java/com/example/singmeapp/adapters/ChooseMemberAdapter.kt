package com.example.singmeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.ChooseMemberItemBinding
import com.example.singmeapp.fragments.ChooseMemberFragment
import com.example.singmeapp.items.User
import com.squareup.picasso.Picasso

class ChooseMemberAdapter(var fragment: Fragment): RecyclerView.Adapter<ChooseMemberAdapter.ChooseMemberHolder>() {
    var userArrayList = ArrayList<User>()

    class ChooseMemberHolder(item: View, fragment: Fragment): RecyclerView.ViewHolder(item){

        val binding = ChooseMemberItemBinding.bind(item)
        var chooseMemberFragment = fragment as ChooseMemberFragment

        fun bind(user: User) = with(binding){
            binding.tvChooseMemberName.text = user.name

            if (user.avatarUrl != ""){
                Picasso.get().load(user.avatarUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivChooseMemberAvatar)
            }

        ChooseMemberLayout.setOnClickListener {
            chooseMemberFragment.binding.tvChosenMemberName.text = user.name
            chooseMemberFragment.binding.tvChosenMemberAge.text = user.age.toString()
            chooseMemberFragment.binding.tvChosenMemberSex.text = user.sex
            chooseMemberFragment.chosenUser = user

            if (user.avatarUrl != ""){
                Picasso.get().load(user.avatarUrl).centerCrop().noFade().noPlaceholder().fit().into(chooseMemberFragment.binding.ivChooseMemberAvatar)
            }

        }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseMemberHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.choose_member_item, parent, false)
        return ChooseMemberHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: ChooseMemberHolder, position: Int) {
        holder.bind(userArrayList[position])
    }

    override fun getItemCount(): Int {
        return userArrayList.size
    }
}