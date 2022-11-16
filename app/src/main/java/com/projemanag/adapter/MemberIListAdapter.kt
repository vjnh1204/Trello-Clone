package com.projemanag.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projemanag.R
import com.projemanag.databinding.ItemMemberBinding
import com.projemanag.models.User

class MemberIListAdapter(private val context: Context,private val memberList: ArrayList<User>): RecyclerView.Adapter<MemberIListAdapter.MemberVH>() {
    inner class MemberVH(private val binding:ItemMemberBinding): RecyclerView.ViewHolder(binding.root) {
            fun bind(position:Int){
                val currentUser = memberList[position]
                binding.tvMemberEmail.text = currentUser.email
                binding.tvMemberName.text = currentUser.name
                Glide.with(context)
                    .load(currentUser.image)
                    .circleCrop()
                    .placeholder(ContextCompat.getDrawable(context,R.drawable.ic_user_place_holder))
                    .into(binding.ivMemberImage)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberVH {
        return MemberVH(ItemMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MemberVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}