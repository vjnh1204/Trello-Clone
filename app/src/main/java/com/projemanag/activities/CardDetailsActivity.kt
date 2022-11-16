package com.projemanag.activities

import android.content.Intent
import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.projemanag.R
import com.projemanag.databinding.ActivityCardDetailsBinding
import com.projemanag.dialogs.LabelColorListDialog
import com.projemanag.firebase.FireStore
import com.projemanag.models.Board
import com.projemanag.models.Card
import com.projemanag.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCardDetailsBinding

    private lateinit var mBoardDetails: Board

    private var mTaskListPosition = -1

    private var mCardListPosition = -1

    private var mSelectedColor = ""

    override fun setLayout(): ViewBinding = ActivityCardDetailsBinding.inflate(layoutInflater)

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityCardDetailsBinding
        getDataFromIntent()
        setUpActionBar()
        binding.etNameCardDetails.setText(mBoardDetails.taskList?.get(mTaskListPosition)!!.cards[mCardListPosition].name)
        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)
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
            mSelectedColor
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
}