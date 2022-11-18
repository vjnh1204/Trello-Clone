package com.projemanag.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.projemanag.R
import com.projemanag.databinding.ItemCardSelectedMemberBinding
import com.projemanag.databinding.ItemMemberBinding
import com.projemanag.models.SelectedMember

class CardMemberListAdapter(
    private val context:Context,
    private val selectedMemberList:ArrayList<SelectedMember>,
    private val assignedMember: Boolean
): RecyclerView.Adapter<CardMemberListAdapter.SelectedMemberVH>() {
    private var onClickItemListener: OnClickItemListener? = null
    inner class SelectedMemberVH(private val binding:ItemCardSelectedMemberBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            if (position ==selectedMemberList.size-1 && assignedMember){
                binding.ivAddMember.visibility = View.VISIBLE
                binding.ivSelectedMemberImage.visibility = View.GONE
            }
            else{
                binding.ivAddMember.visibility = View.GONE
                binding.ivSelectedMemberImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(selectedMemberList[position].image)
                    .circleCrop()
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_user_place_holder))
                    .into(binding.ivSelectedMemberImage)
            }
            binding.ivAddMember.setOnClickListener {
                if (onClickItemListener!= null){
                    onClickItemListener!!.onClick()
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedMemberVH {
        return SelectedMemberVH(ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SelectedMemberVH, position: Int) {
       holder.bind(position)
    }

    override fun getItemCount(): Int {
       return selectedMemberList.size
    }
    interface OnClickItemListener{
        fun onClick()
    }
    fun setOnItemClickListener(onClickItemListener: OnClickItemListener){
        this.onClickItemListener = onClickItemListener
    }
}