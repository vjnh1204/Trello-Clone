package com.projemanag.activities

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.projemanag.R
import com.projemanag.databinding.ActivityUserProfileBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.User
import com.projemanag.utils.Constants

class UserProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private var mSelectedImageURI :Uri? = null
    private var mProfileImageURL : String = ""
    private lateinit var mUserDetails: User
    var openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            mSelectedImageURI = result.data?.data
            try {
                Glide.with(this@UserProfileActivity)
                    .load(mSelectedImageURI)
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivUserImage)
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    override fun setLayout(): ViewBinding {
        return ActivityUserProfileBinding.inflate(layoutInflater)
    }

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityUserProfileBinding
        setUpActionBar()
        FireStore().loadUserData(this)
        binding.ivUserImage.setOnClickListener {
            Constants.pickImageChooser(this@UserProfileActivity)
        }
        binding.btnUpdate.setOnClickListener {
            if (mSelectedImageURI != null){
                uploadUserImage()
            }
            else{
                updateUserProfileData()
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarMyProfileActivity)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun setUserDataInUI(user: User){
        mUserDetails = user
        Glide
            .with(this@UserProfileActivity)
            .load(user.image)
            .circleCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivUserImage)
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L){
            binding.etMobile.setText(user.mobile.toString())
        }

    }
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageURI != null) {

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(
                    this,mSelectedImageURI
                )
            )

            sRef.putFile(mSelectedImageURI!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())
                            mProfileImageURL = uri.toString()
                            updateUserProfileData()
                            hideProgressDialog()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@UserProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
        }
    }

    fun profileUpdateSuccess() {
        setResult(RESULT_OK)
        hideProgressDialog()
        Toast.makeText(this,"User Profile update successfully",Toast.LENGTH_SHORT).show()
        finish()
    }
    private fun updateUserProfileData() {

        val userHashMap = HashMap<String, Any>()

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        if (binding.etName.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = binding.etName.text.toString()
        }

        if (binding.etMobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = binding.etMobile.text.toString().toLong()
        }

        // Update the data in the database.
        FireStore().updateUserProfileData(this, userHashMap)
    }
}