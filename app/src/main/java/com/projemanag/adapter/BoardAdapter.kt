package com.projemanag.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projemanag.databinding.ItemBoardBinding
import com.projemanag.models.Board

class BoardAdapter(
    private val context: Context,
    private val boardList: ArrayList<Board>
) : RecyclerView.Adapter<BoardAdapter.BoardVH>() {
    private var onBoardClickListener: OnBoardClickListener? = null

    inner class BoardVH(private val binding: ItemBoardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Board) {
            Glide.with(context)
                .load(item.image)
                .circleCrop()
                .into(binding.ivBoardImage)
            binding.tvName.text = item.name
            binding.tvCreatedBy.text = item.createBy
            binding.root.setOnClickListener {
                if (onBoardClickListener != null) {
                    onBoardClickListener!!.onClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardVH {
        return BoardVH(ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BoardVH, position: Int) {
        val item = boardList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return boardList.size
    }

    interface OnBoardClickListener {
        fun onClick(item: Board)
    }
    fun setOnClickListener(onBoardClickListener: OnBoardClickListener){
        this.onBoardClickListener = onBoardClickListener
    }
}