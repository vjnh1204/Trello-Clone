package com.projemanag.activities

import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.viewbinding.ViewBinding
import com.projemanag.R
import com.projemanag.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun setLayout(): ViewBinding  = ActivityMainBinding.inflate(layoutInflater)

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityMainBinding
        setUpActionBar()
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.appBarMain.toolbarMainActivity)
        binding.appBarMain.toolbarMainActivity.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_action_navigation)
        binding.appBarMain.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }
    private fun toggleDrawer(){
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
}
