package com.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.projemanag.activities.*
import com.projemanag.models.Board
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
    fun loadUserData(activity: Activity,isToReadBoardsList: Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUser())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val loggedInUser = documentSnapshot.toObject(User::class.java)
                if (loggedInUser!= null){
                    when(activity){
                        is MainActivity ->{
                            activity.updateNavHeaderUser(loggedInUser,isToReadBoardsList)
                        }
                        is SignInActivity ->{
                            activity.signInSuccess(loggedInUser)
                        }
                        is UserProfileActivity ->{
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }
            }
            .addOnFailureListener {
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
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

    fun updateUserProfileData(userProfileActivity: UserProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUser())
            .update(userHashMap)
            .addOnSuccessListener {
                userProfileActivity.profileUpdateSuccess()

            }
            .addOnFailureListener {
                Log.e("Error",it.message!!)
            }
    }
    fun createBoard(createBoardActivity: CreateBoardActivity,board: Board){
        mFireStore.collection(Constants.BOARD)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                createBoardActivity.createBoardSuccessfully()
                Toast.makeText(createBoardActivity,"Board create successfully",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e("Error",it.message!!)
            }
    }
    fun getBoardList(activity: MainActivity){
        mFireStore.collection(Constants.BOARD)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUser())
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val boardList = ArrayList<Board>()
                for(i in document){
                    val board = i.toObject(Board::class.java)
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e("Error",it.message!!)
            }
    }
    fun getBoardDetail(activity: TaskListActivity,documentId:String){
        mFireStore.collection(Constants.BOARD)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                document->
                Log.e(activity.javaClass.simpleName, document.toString())
                val board = document.toObject(Board::class.java)
                board?.documentId = document.id
                activity.boardDetail(board!!)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e("Error",it.message!!)
            }
    }
    fun addUpdateTaskList(activity: TaskListActivity,board: Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList!!

        mFireStore.collection(Constants.BOARD)
            .document(board.documentId!!)
            .update(taskListHashMap)
            .addOnSuccessListener {
                activity.addUpdateTaskListSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
            }

    }
}