package com.projemanag.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ContentInfoCompat.Flags
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.projemanag.R
import com.projemanag.adapter.BoardAdapter
import com.projemanag.databinding.ActivityMainBinding
import com.projemanag.databinding.ContentMainBinding
import com.projemanag.databinding.NavHeaderMainBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.Board
import com.projemanag.models.User
import com.projemanag.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mUSerName : String
    private var startActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if (result.resultCode == RESULT_OK ){
                    FireStore().loadUserData(this@MainActivity)
            }
            else{
                Log.e("Canceled","Canceled")
            }
    }
    private var createBoardActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if (result.resultCode == RESULT_OK){
                FireStore().getBoardList(this)
            }
    }

    private lateinit var mSharedPreferences: SharedPreferences
    override fun setLayout(): ViewBinding  = ActivityMainBinding.inflate(layoutInflater)

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityMainBinding
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().loadUserData(this@MainActivity, true)
        setUpActionBar()
        binding.appBarMain.fabCreateBoard.setOnClickListener {
            val intent = Intent(this@MainActivity,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUSerName)
            createBoardActivityLauncher.launch(intent)
        }
        mSharedPreferences = this.getSharedPreferences(Constants.PROGEMANAG_PREFERENCES, Context.MODE_PRIVATE)
        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)
        hideProgressDialog()
        // Here if the token is already updated than we don't need to update it every time.
        if (tokenUpdated) {
            // Get the current logged in user details.
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStore().loadUserData(this@MainActivity, true)
        } else {
            FirebaseInstanceId.getInstance()
                .instanceId.addOnSuccessListener(this@MainActivity) { instanceIdResult ->
                    updateFCMToken(instanceIdResult.token)
                }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.appBarMain.toolbarMainActivity)
        binding.appBarMain.toolbarMainActivity.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_action_navigation)
        binding.appBarMain.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
        binding.navView.setNavigationItemSelectedListener(this)
    }
    private fun toggleDrawer(){
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun updateNavHeaderUser(user: User,isToReadBoardsList: Boolean){
        hideProgressDialog()
        mUSerName = user.name!!
        val navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
            Glide
                .with(this)
                .load(user.image)
                .circleCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(navHeaderMainBinding.ivUserImage)
        navHeaderMainBinding.tvUsername.text = user.name

        if (isToReadBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStore().getBoardList(this@MainActivity)
        }
    }
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
                startActivityLauncher.launch(Intent(this@MainActivity,UserProfileActivity::class.java))
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()
                val intent = Intent(this@MainActivity,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    fun populateBoardsListToUI(boardList:ArrayList<Board>){
        hideProgressDialog()
        if (boardList.size > 0) {

            binding.appBarMain.contentMain.rvBoardsList.visibility = View.VISIBLE
            binding.appBarMain.contentMain.tvNoBoardsAvailable.visibility = View.GONE

            binding.appBarMain.contentMain.rvBoardsList.layoutManager = LinearLayoutManager(this@MainActivity)
            binding.appBarMain.contentMain.rvBoardsList.setHasFixedSize(true)

            // Create an instance of BoardItemsAdapter and pass the boardList to it.
            val adapter = BoardAdapter(this@MainActivity, boardList)
            adapter.setOnClickListener(object : BoardAdapter.OnBoardClickListener{
                override fun onClick(item: Board) {
                    val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,item.documentId)
                    startActivity(intent)
                }

            })
            binding.appBarMain.contentMain.rvBoardsList.adapter = adapter // Attach the adapter to the recyclerView.
        } else {
            binding.appBarMain.contentMain.rvBoardsList.visibility = View.GONE
            binding.appBarMain.contentMain.tvNoBoardsAvailable.visibility = View.VISIBLE
        }

    }
    fun tokenUpdateSuccess() {

        hideProgressDialog()

        // Here we have added a another value in shared preference that the token is updated in the database successfully.
        // So we don't need to update it every time.
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()

        // Get the current logged in user details.
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().loadUserData(this@MainActivity, true)
    }
    private fun updateFCMToken(token: String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token

        // Update the data in the database.
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStore().updateUserProfileData(this@MainActivity, userHashMap)
    }
}
