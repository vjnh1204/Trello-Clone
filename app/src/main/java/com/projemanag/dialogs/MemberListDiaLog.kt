package com.projemanag.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.projemanag.adapter.MemberIListAdapter
import com.projemanag.databinding.DialogListBinding
import com.projemanag.models.User

abstract class MemberListDiaLog(context: Context, private val title:String, private val assignedMemberList:ArrayList<User>): Dialog(context){
    private lateinit var binding: DialogListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogListBinding.inflate(LayoutInflater.from(context),null,false)
        setContentView(binding.root)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(context)
        val adapter = MemberIListAdapter(context,assignedMemberList)
        binding.rvList.adapter = adapter
        adapter.setOnItemClickListener(object : MemberIListAdapter.OnClickListener{
            override fun onClick(position: Int, user: User, action: String) {
                dismiss()
                onItemSelected(user,action)
            }

        })

    }

    protected abstract fun onItemSelected(user: User,action:String)
}