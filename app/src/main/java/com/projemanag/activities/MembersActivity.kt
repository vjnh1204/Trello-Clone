package com.projemanag.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.DialogCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.projemanag.R
import com.projemanag.adapter.MemberIListAdapter
import com.projemanag.databinding.ActivityMembersBinding
import com.projemanag.databinding.DialogSearchMemberBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.Board
import com.projemanag.models.User
import com.projemanag.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var binding: ActivityMembersBinding

    private lateinit var mBoardDetails: Board
    private var anyChange = false
    private lateinit var mAssignedMemberList : ArrayList<User>
    override fun setLayout(): ViewBinding {
        return ActivityMembersBinding.inflate(layoutInflater)
    }

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityMembersBinding
        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        setUpActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().getAssignedMembersListDetail(this,mBoardDetails.assignedTo!!)
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarMembersActivity)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title = resources.getString(R.string.members)
        }
        binding.toolbarMembersActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun memberDetails(user: User){
        mBoardDetails.assignedTo?.add(user.id!!)
        FireStore().assignedMemberToBoard(this,mBoardDetails,user)
    }
    fun setUpMembersList(list : ArrayList<User>){
        mAssignedMemberList = list

        hideProgressDialog()
        binding.rvMembersList.apply {
            layoutManager = LinearLayoutManager(this@MembersActivity)
            setHasFixedSize(true)
            adapter = MemberIListAdapter(context,list)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun dialogSearchMember(){
        val inflater = this@MembersActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogSearchMemberBinding = DialogSearchMemberBinding.inflate(inflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogSearchMemberBinding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialogSearchMemberBinding.tvAdd.setOnClickListener {
            val email = dialogSearchMemberBinding.etEmailSearchMember.text.toString()
            if (email.isNotEmpty()){
                hideProgressDialog()
                FireStore().getMemberDetails(this,email)
            }
            dialog.dismiss()
        }
        dialogSearchMemberBinding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    fun memberAssignedSuccess(user: User){
        hideProgressDialog()
        mAssignedMemberList.add(user)
        anyChange = true
        setUpMembersList(mAssignedMemberList)
    }

    override fun onBackPressed() {
        if (anyChange){
            val intent = Intent()
            setResult(RESULT_OK,intent)
        }
        super.onBackPressed()
    }
}