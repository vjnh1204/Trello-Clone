package com.projemanag.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.projemanag.R
import com.projemanag.adapter.CardMemberListAdapter
import com.projemanag.databinding.ActivityCardDetailsBinding
import com.projemanag.dialogs.LabelColorListDialog
import com.projemanag.dialogs.MemberListDiaLog
import com.projemanag.firebase.FireStore
import com.projemanag.models.Board
import com.projemanag.models.Card
import com.projemanag.models.SelectedMember
import com.projemanag.models.User
import com.projemanag.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class CardDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCardDetailsBinding

    private lateinit var mBoardDetails: Board

    private var mTaskListPosition = -1

    private var mCardListPosition = -1

    private var mSelectedColor = ""

    private lateinit var mAssignedMemberList : ArrayList<User>

    private var mSelectedDueDateMilliSeconds : Long = 0
    override fun setLayout(): ViewBinding = ActivityCardDetailsBinding.inflate(layoutInflater)

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityCardDetailsBinding
        getDataFromIntent()
        setUpActionBar()
        binding.etNameCardDetails.setText(mBoardDetails.taskList?.get(mTaskListPosition)!!.cards[mCardListPosition].name)
        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)
        mSelectedColor = mBoardDetails.taskList?.get(mTaskListPosition)!!.cards[mCardListPosition].labelColor
        if (mSelectedColor.isNotEmpty()){
            setColor()
        }
        binding.btnUpdateCardDetails.setOnClickListener {
            if (binding.etNameCardDetails.text!!.isNotEmpty()) {
                updateCardList()
            } else {
                Toast.makeText(this, "Please enter card name", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvSelectLabelColor.setOnClickListener {
            labelColorListDialog()
        }
        binding.tvSelectMembers.setOnClickListener {
            assignedMemberListDialog()
        }
        setUpSelectedMemberList()
        binding.tvSelectDueDate.setOnClickListener {
            datePickerDialog()
        }
        mSelectedDueDateMilliSeconds = mBoardDetails.taskList!![mTaskListPosition].cards[mCardListPosition].dueDate
        if (mSelectedDueDateMilliSeconds > 0){
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val dueDate = sdf.format(mSelectedDueDateMilliSeconds)
            binding.tvSelectDueDate.text = dueDate
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarCardDetailsActivity)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title =
                mBoardDetails.taskList?.get(mTaskListPosition)!!.cards[mCardListPosition].name
        }
        binding.toolbarCardDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_POSITION)) {
            mCardListPosition = intent.getIntExtra(Constants.CARD_LIST_POSITION, -1)
        }
        if(intent.hasExtra(Constants.BOARD_ASSIGNED_MEMBER)){
            mAssignedMemberList = intent.getParcelableArrayListExtra<User>(Constants.BOARD_ASSIGNED_MEMBER)!!
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                deleteCard()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateCardListSuccess() {
        hideProgressDialog()
        setResult(RESULT_OK, Intent())
        finish()
    }

    private fun updateCardList() {
        val card = Card(
            binding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList?.get(mTaskListPosition)!!.cards[mCardListPosition].createBy,
            mBoardDetails.taskList?.get(mTaskListPosition)!!.cards[mCardListPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )
        mBoardDetails.taskList?.get(mTaskListPosition)!!.cards[mCardListPosition] = card
        mBoardDetails.taskList?.removeAt(mBoardDetails.taskList?.size!!.minus(1))

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard() {
        mBoardDetails.taskList?.get(mTaskListPosition)!!.cards.removeAt(mCardListPosition)
        mBoardDetails.taskList?.removeAt(mBoardDetails.taskList?.size!!.minus(1))

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun colorList() : ArrayList<String>{
        val colorList = ArrayList<String>()
        colorList.add("#43C86F")
        colorList.add("#0C90F1")
        colorList.add("#F72400")
        colorList.add("#7A8089")
        colorList.add("#D57C1D")
        colorList.add("#770000")
        colorList.add("#0022F8")
        return colorList
    }

    private fun setColor(){
        binding.tvSelectLabelColor.text = ""
        binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorListDialog(){
        val colorList = colorList()

        val listDialog = object : LabelColorListDialog(
            this,
            resources.getString(R.string.str_select_label_color),
            colorList,
            mSelectedColor){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }
        listDialog.show()
    }

    private fun assignedMemberListDialog(){
        val cardAssignedMembersList =
            mBoardDetails.taskList?.get(mTaskListPosition)!!.cards[mCardListPosition].assignedTo
        if (cardAssignedMembersList.size > 0){
            for (i in mAssignedMemberList.indices){
                for(j in cardAssignedMembersList){
                    if (mAssignedMemberList[i].id == j){
                        mAssignedMemberList[i].selected = true
                    }
                }
            }
        }
        else{
            for (i in mAssignedMemberList.indices){
                mAssignedMemberList[i].selected = false
            }
        }
        val memberListDialog = object : MemberListDiaLog(this,resources.getString(R.string.str_select_member),mAssignedMemberList){
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECTED) {
                    if (!mBoardDetails.taskList!![mTaskListPosition].cards[mCardListPosition].assignedTo.contains(
                            user.id
                        )
                    ) {
                        mBoardDetails.taskList!![mTaskListPosition].cards[mCardListPosition].assignedTo.add(
                            user.id!!
                        )
                    }
                } else {
                    mBoardDetails.taskList!![mTaskListPosition].cards[mCardListPosition].assignedTo.remove(
                        user.id
                    )

                    for (i in mAssignedMemberList.indices) {
                        if (mAssignedMemberList[i].id == user.id) {
                            mAssignedMemberList[i].selected = false
                        }
                    }
                }

                setUpSelectedMemberList()
            }

        }
        memberListDialog.show()
        Log.d("AAA",mAssignedMemberList.toString())
    }
    private fun setUpSelectedMemberList(){
        val cardAssignedMemberList = mBoardDetails.taskList?.get(mTaskListPosition)!!.cards[mCardListPosition].assignedTo

        val selectedMemberList = ArrayList<SelectedMember>()

        for (i in mAssignedMemberList.indices) {
            for (j in cardAssignedMemberList) {
                if (mAssignedMemberList[i].id == j) {
                    val selectedMember = SelectedMember(
                        mAssignedMemberList[i].id!!,
                        mAssignedMemberList[i].image!!
                    )

                    selectedMemberList.add(selectedMember)
                }
            }
        }
        if(selectedMemberList.size > 0){
            selectedMemberList.add(SelectedMember("", ""))
            binding.tvSelectMembers.visibility = View.GONE
            binding.rvSelectedMembersList.visibility = View.VISIBLE
            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this,6)
            val adapter = CardMemberListAdapter(this,selectedMemberList,true)
            binding.rvSelectedMembersList.adapter = adapter
            adapter.setOnItemClickListener(object : CardMemberListAdapter.OnClickItemListener{
                override fun onClick() {
                    assignedMemberListDialog()
                }

            })
        }
        else{
            binding.tvSelectMembers.visibility = View.VISIBLE
            binding.rvSelectedMembersList.visibility = View.GONE
        }
    }
    private fun datePickerDialog(){
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val sDayOfMonth = if(dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if (month <10) "0$month" else "$month"
                val selectedDay = "$sDayOfMonth/$sMonthOfYear/$year"
                val theDate = sdf.parse(selectedDay)
                binding.tvSelectDueDate.text = selectedDay
                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            dayOfMonth
        )
        datePicker.show()
    }
}