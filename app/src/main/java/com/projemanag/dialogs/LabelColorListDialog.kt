package com.projemanag.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.projemanag.adapter.LabelColorListItemAdapter
import com.projemanag.databinding.DialogListBinding

abstract class LabelColorListDialog(
    context: Context,
    private val title: String = "",
    private var listColor: ArrayList<String>,
    private var mSelectedColor: String = ""
) : Dialog(context) {
    private lateinit var binding: DialogListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogListBinding.inflate(LayoutInflater.from(context), null, false)
        setContentView(binding.root)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(context)
        val adapter = LabelColorListItemAdapter(context, listColor, mSelectedColor)
        adapter.setOnColorClickListener(object :
            LabelColorListItemAdapter.OnColorItemClickListener {
            override fun onClick(position: Int, color: String) {
                onItemSelected(color)
                dismiss()
            }
        })
        binding.rvList.adapter = adapter
    }

    protected abstract fun onItemSelected(color: String)
}