package com.projemanag.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.databinding.ItemCardBinding
import com.projemanag.models.Card

class CardListItemAdapter(private val context: Context,private val cardList: ArrayList<Card>) :RecyclerView.Adapter<CardListItemAdapter.CardVH>(){
    private var onCardClickListener: OnCardClickListener? = null
    inner class CardVH(private val binding: ItemCardBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.tvCardName.text = cardList[position].name
            binding.root.setOnClickListener {
                if (onCardClickListener != null){
                    onCardClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardVH {
        return CardVH(ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CardVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
    interface OnCardClickListener{
        fun onClick(cardPosition:Int)
    }
    fun setOnCardClickListener(onCardClickListener: OnCardClickListener){
        this.onCardClickListener = onCardClickListener
    }
}