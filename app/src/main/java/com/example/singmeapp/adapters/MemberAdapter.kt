package com.example.singmeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.singmeapp.R
import com.example.singmeapp.databinding.MemberItemBinding
import com.example.singmeapp.items.Member
import com.squareup.picasso.Picasso

class MemberAdapter: RecyclerView.Adapter<MemberAdapter.MemberHolder>() {

    var memberList = ArrayList<Member>()

    class MemberHolder(item: View): RecyclerView.ViewHolder(item){

        val binding = MemberItemBinding.bind(item)

        fun bind(member: Member) = with(binding){
            tvMemberName.text = member.name
            tvMemberRoles.text = member.role
            if (member.avatarUrl != ""){
                Picasso.get().load(member.avatarUrl).centerCrop().noFade().noPlaceholder().fit().into(binding.ivMemberAvatar)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member_item, parent, false)
        return MemberHolder(view)
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.bind(memberList[position])
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

}