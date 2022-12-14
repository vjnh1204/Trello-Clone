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
    const val BOARD_DETAIL: String = "board_detail"
    const val ID:String = "id"
    const val EMAIL: String = "email"
    const val TASK_LIST_POSITION :String = "taskListPosition"
    const val CARD_LIST_POSITION : String = "cardListPosition"
    const val BOARD_ASSIGNED_MEMBER : String = "boardAssignedMember"
    const val SELECTED : String  = "selected"
    const val UNSELECTED : String = "unselected"
    const val PROGEMANAG_PREFERENCES: String = "ProjemanagPrefs"
    const val FCM_TOKEN:String = "fcmToken"
    const val FCM_TOKEN_UPDATED:String = "fcmTokenUpdated"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "BO-CM2LZUIDrKho7T7xzdQ7wITOEOJTgoQFHDTvSq-5_qAQFWX_7RdDNtkLHCy-4hWEJmXhAcYWwu3XQEq_1-vY"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"
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