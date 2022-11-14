package com.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.projemanag.R
import com.projemanag.adapter.TaskListItemsAdapter
import com.projemanag.databinding.ActivityTaskListBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.Board
import com.projemanag.models.Task
import com.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding

    private lateinit var mBoardDetails : Board
    override fun setLayout(): ViewBinding = ActivityTaskListBinding.inflate(layoutInflater)

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityTaskListBinding
        var boardDocumentId =""
        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().getBoardDetail(this,boardDocumentId)
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarTaskListActivity)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title = mBoardDetails.name
        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun boardDetail(board: Board){
        mBoardDetails = board
        hideProgressDialog()
        setUpActionBar()

        val addTaskList = Task(resources.getString(R.string.please_wait))
        board.taskList?.add(addTaskList)
        binding.rvTaskList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvTaskList.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this@TaskListActivity,board.taskList!!)
        binding.rvTaskList.adapter = adapter
    }
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().getBoardDetail(this,mBoardDetails.documentId!!)
    }
    fun createTaskList(taskListName: String){
        val task = Task(taskListName,FireStore().getCurrentUser())
        mBoardDetails.taskList?.add(0,task)
        mBoardDetails.taskList?.removeAt(mBoardDetails.taskList?.size!!.minus(1))

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().addUpdateTaskList(this,mBoardDetails)
    }
    fun updateTaskList(position:Int,listName:String,model:Task){
        val task = Task(listName,model.createBy)

        mBoardDetails.taskList?.set(position, task)
        mBoardDetails.taskList?.removeAt(mBoardDetails.taskList?.size!!.minus(1))

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStore().addUpdateTaskList(this,mBoardDetails)
    }

}