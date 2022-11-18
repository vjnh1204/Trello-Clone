package com.projemanag.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.activities.TaskListActivity
import com.projemanag.databinding.ItemCardBinding
import com.projemanag.models.Card
import com.projemanag.models.SelectedMember

class CardListItemAdapter(private val context: Context,private val cardList: ArrayList<Card>) :RecyclerView.Adapter<CardListItemAdapter.CardVH>(){
    private var onCardClickListener: OnCardClickListener? = null
    inner class CardVH(private val binding: ItemCardBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            if (cardList[position].labelColor.isNotEmpty()){
                binding.viewLabelColor.visibility = View.VISIBLE
                binding.viewLabelColor.setBackgroundColor(Color.parseColor(cardList[position].labelColor))
            }
            else{
                binding.viewLabelColor.visibility = View.GONE
            }
            val model = cardList[position]
            if ((context as TaskListActivity).mAssignedMemberDetailsList.size > 0) {

                val selectedMembersList: ArrayList<SelectedMember> = ArrayList()

                // Here we got the detail list of members and add it to the selected members list as required.
                for (i in context.mAssignedMemberDetailsList.indices) {
                    for (j in model.assignedTo) {
                        if (context.mAssignedMemberDetailsList[i].id == j) {
                            val selectedMember = SelectedMember(
                                context.mAssignedMemberDetailsList[i].id!!,
                                context.mAssignedMemberDetailsList[i].image!!
                            )

                            selectedMembersList.add(selectedMember)
                        }
                    }
                }

                if (selectedMembersList.size > 0) {

                    if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createBy) {
                        binding.rvCardSelectedMembersList.visibility = View.GONE
                    } else {
                        binding.rvCardSelectedMembersList.visibility = View.VISIBLE

                        binding.rvCardSelectedMembersList.layoutManager =
                            GridLayoutManager(context, 4)

                        val adapter = CardMemberListAdapter(context, selectedMembersList, false)
                        binding.rvCardSelectedMembersList.adapter = adapter
                        adapter.setOnItemClickListener(object : CardMemberListAdapter.OnClickItemListener{
                            override fun onClick() {
                                if (onCardClickListener != null) {
                                    onCardClickListener!!.onClick(position)
                                }
                            }
                        })
                        Log.d("AAA",selectedMembersList.toString())
                    }
                } else {
                    binding.rvCardSelectedMembersList.visibility = View.GONE
                }
            }
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