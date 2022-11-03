package com.projemanag.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.projemanag.activities.SignInActivity
import com.projemanag.activities.SignUpActivity
import com.projemanag.models.User
import com.projemanag.utils.Constants

class FireStore {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity,userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUser())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                Log.e("Error",it.message!!)
            }
    }
    fun loginUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUser())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val loggedInUser = documentSnapshot.toObject(User::class.java)
                if (loggedInUser!= null){
                    activity.signInSuccess(loggedInUser)
                }
            }
            .addOnFailureListener {
                Log.e("Error",it.message!!)
            }
    }
    fun getCurrentUser():String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserUid = ""
        if (currentUser != null){
            currentUserUid = currentUser.uid
        }
        return currentUserUid
    }
}