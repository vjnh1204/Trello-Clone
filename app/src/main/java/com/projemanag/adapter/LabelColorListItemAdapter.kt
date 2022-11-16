package com.projemanag.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.databinding.ItemLableColorBinding

class LabelColorListItemAdapter(
    private val context: Context,
    private val listColor: ArrayList<String>,
    private val mSelectedColor: String
) : RecyclerView.Adapter<LabelColorListItemAdapter.ColorVH>() {
    private var onColorItemClickListener : OnColorItemClickListener? = null
    inner class ColorVH(private val binding: ItemLableColorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val colorItem = listColor[position]
            binding.viewMain.setBackgroundColor(Color.parseColor(colorItem))
            if (mSelectedColor == colorItem){
                binding.ivSelectedColor.visibility = View.VISIBLE
            }
            else{
                binding.ivSelectedColor.visibility = View.GONE
            }
            binding.root.setOnClickListener {
                if(onColorItemClickListener != null){
                    onColorItemClickListener?.onClick(position,colorItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorVH {
        return ColorVH(
            ItemLableColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ColorVH, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return listColor.size
    }

    interface OnColorItemClickListener{
        fun onClick(position: Int, color: String)
    }

    fun setOnColorClickListener(onColorItemClickListener: OnColorItemClickListener){
        this.onColorItemClickListener = onColorItemClickListener
    }
}