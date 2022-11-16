package com.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.projemanag.R
import com.projemanag.adapter.TaskListItemsAdapter
import com.projemanag.databinding.ActivityTaskListBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.Board
import com.projemanag.models.Card
import com.projemanag.models.Task
import com.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding

    private lateinit var mBoardDetails : Board
    private lateinit var mBoardDocumentId : String
    private val reloadBoard = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == RESULT_OK && result.data != null ){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStore().getBoardDetail(this,mBoardDocumentId)
        }
        else{
            Log.e("Cancel","Cancel")
        }
    }
    override fun setLayout(): ViewBinding = ActivityTaskListBinding.inflate(layoutInflater)

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityTaskListBinding
        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().getBoardDetail(this,mBoardDocumentId)
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
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList?.removeAt(position)
        mBoardDetails.taskList?.removeAt(mBoardDetails.taskList?.size!!.minus(1))

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStore().addUpdateTaskList(this,mBoardDetails)
    }
    fun createCardList(position: Int,cardName: String){
        mBoardDetails.taskList?.removeAt(mBoardDetails.taskList?.size!!.minus(1))

        val cardAssignedList = ArrayList<String>()
        cardAssignedList.add(FireStore().getCurrentUser())
        val card = Card(cardName,FireStore().getCurrentUser(),cardAssignedList)
        val cardList = mBoardDetails.taskList?.get(position)?.cards
        cardList?.add(card)
        val task = Task(mBoardDetails.taskList?.get(position)?.tile,mBoardDetails.taskList?.get(position)?.createBy,cardList!!)
        mBoardDetails.taskList?.set(position, task)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().addUpdateTaskList(this,mBoardDetails)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent = Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                reloadBoard.launch(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun cardDetails(taskListPosition: Int, cardListPosition:Int){
        val intent = Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
        intent.putExtra(Constants.CARD_LIST_POSITION,cardListPosition)
        intent.putExtra(Constants.TASK_LIST_POSITION,taskListPosition)
        reloadBoard.launch(intent)
    }
}