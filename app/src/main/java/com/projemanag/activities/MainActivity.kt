package com.projemanag.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.ContentInfoCompat.Flags
import androidx.core.view.GravityCompat
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import com.projemanag.databinding.ActivityMainBinding
import com.projemanag.databinding.NavHeaderMainBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.User

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun setLayout(): ViewBinding  = ActivityMainBinding.inflate(layoutInflater)

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityMainBinding
        FireStore().loadUserData(this)
        setUpActionBar()
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
    @SuppressLint("SuspiciousIndentation")
    fun updateNavHeaderUser(user: User){
        val navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))

        Log.d("AAA",user.image.toString())
            Glide
                .with(this)
                .load(user.image)
                .circleCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(navHeaderMainBinding.ivUserImage)
        navHeaderMainBinding.tvUsername.text = user.name
        Log.d("AAA",binding.navView.headerCount.toString())
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
                startActivity(Intent(this@MainActivity,UserProfileActivity::class.java))
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
