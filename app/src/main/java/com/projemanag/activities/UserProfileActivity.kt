package com.projemanag.activities

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.projemanag.R
import com.projemanag.databinding.ActivityUserProfileBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.User

class UserProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private var openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            Toast.makeText(this,"AAAA",Toast.LENGTH_SHORT).show()
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
            pickImageChooser()
        }
    }
    private fun pickImageChooser(){
        Dexter.withContext(this@UserProfileActivity)
            .withPermissions(mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if(p0!!.areAllPermissionsGranted()){
                        try {
                            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            openGalleryLauncher.launch(intent)
                        }
                        catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationaleDialogForPermissions()
                }


            }).onSameThread().check()
    }
    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this).setMessage(
            "" +
                    "It looks like you have turned off permissions required" +
                    "for this feature. It can be enable under the" +
                    "Applications Settings. "
        )
            .setPositiveButton("GO TO SETTINGS.") { _, _ ->
                try {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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
        Glide
            .with(this)
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
}