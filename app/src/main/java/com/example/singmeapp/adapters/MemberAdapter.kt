package com.example.singmeapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.MemberItemBinding
import com.example.singmeapp.items.Member
import com.example.singmeapp.viewmodels.BandViewModel
import com.squareup.picasso.Picasso

class MemberAdapter(var fragment: Fragment): RecyclerView.Adapter<MemberAdapter.MemberHolder>() {

    var memberList = ArrayList<Member>()

    class MemberHolder(item: View, var fragment: Fragment): RecyclerView.ViewHolder(item){

        val binding = MemberItemBinding.bind(item)
        lateinit var bandViewModel: BandViewModel


        fun bind(member: Member) = with(binding){
            tvMemberName.text = member.name
            tvMemberRoles.text = member.role
            if (member.avatarUrl != ""){
                Picasso.get().load(member.avatarUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivMemberAvatar)
            }
            val provider = ViewModelProvider(fragment)
            bandViewModel = provider[BandViewModel::class.java]
            bandViewModel.isEdit.observe(fragment.viewLifecycleOwner){
                if (it == true){
                   binding.deleteMember.visibility = View.VISIBLE
                }
                else binding.deleteMember.visibility = View.GONE
            }
            binding.deleteMember.setOnClickListener{
                Log.e("Delete", member.uuid)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member_item, parent, false)
        return MemberHolder(view, fragment)
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.bind(memberList[position])
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

}