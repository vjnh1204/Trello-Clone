package com.projemanag.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.projemanag.activities.CreateBoardActivity
import com.projemanag.activities.UserProfileActivity

object Constants {
    const val MOBILE: String ="mobile"
    const val NAME: String = "name"
    const val IMAGE: String = "image"
    const val USERS :String = "users"
    const val BOARD :String = "board"
    const val ASSIGNED_TO : String = "assignedTo"
    const val DOCUMENT_ID : String = "documentId"
    const val TASK_LIST : String = "taskList"
    fun getFileExtension(activity: Activity,uri: Uri?) : String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun pickImageChooser(activity: Activity){
        Dexter.withContext(activity)
            .withPermissions(mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if(p0!!.areAllPermissionsGranted()){
                        try {
                            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            when(activity){
                                is UserProfileActivity->{
                                    activity.openGalleryLauncher.launch(intent)
                                }
                                is CreateBoardActivity -> {
                                    activity.openGalleryLauncher.launch(intent)
                                }
                            }

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
                    showRationaleDialogForPermissions(activity)
                }


            }).onSameThread().check()
    }
    private fun showRationaleDialogForPermissions(activity: Activity) {
        AlertDialog.Builder(activity).setMessage(
            "" +
                    "It looks like you have turned off permissions required" +
                    "for this feature. It can be enable under the" +
                    "Applications Settings. "
        )
            .setPositiveButton("GO TO SETTINGS.") { _, _ ->
                try {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${activity.packageName}")
                    )
                    activity.startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}