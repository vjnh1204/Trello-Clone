package com.projemanag.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.projemanag.R
import com.projemanag.databinding.ActivityCreateBoardBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.Board
import com.projemanag.utils.Constants

class CreateBoardActivity : BaseActivity() {
    private var binding:ActivityCreateBoardBinding? = null
    private var mSelectedImageURI : Uri? = null
    private var mImageUri : String = ""
    var openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            mSelectedImageURI = result.data?.data
            try {
                Glide.with(this@CreateBoardActivity)
                    .load(mSelectedImageURI)
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding!!.ivBoardImage)
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    override fun setLayout(): ViewBinding {
        return ActivityCreateBoardBinding.inflate(layoutInflater)
    }

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityCreateBoardBinding
        setUpActionBar()
        binding?.ivBoardImage?.setOnClickListener {
            Constants.pickImageChooser(this@CreateBoardActivity)
        }
        binding?.btnCreate?.setOnClickListener {
            if(mSelectedImageURI != null){
                updateImageUri()
            }
            else{
                createBoard()
            }
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            supportActionBar?.title = resources.getString(R.string.create_board_title)
        }
        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

    }
    private fun updateImageUri(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if(mSelectedImageURI != null){
            val sRef :StorageReference = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE"
                        + System.currentTimeMillis()
                        + Constants.getFileExtension(this,mSelectedImageURI)
            )
            sRef.putFile(mSelectedImageURI!!).addOnSuccessListener {
                taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        mImageUri = uri.toString()
                        createBoard()

                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()

                        hideProgressDialog()
                    }
            }
        }
    }
    private fun createBoard(){
        val assignedUsersArrayList : ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())
        val mUserName = intent.getStringExtra(Constants.NAME)
        val board = Board(
            binding?.etBoardName?.text.toString(),
            mImageUri,
            mUserName,
            assignedUsersArrayList
        )
        FireStore().createBoard(this,board)
    }
    fun createBoardSuccessfully(){
        hideProgressDialog()
        setResult(RESULT_OK)
        finish()
    }
}