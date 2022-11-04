package com.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.projemanag.R
import com.projemanag.databinding.ActivitySignUpBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.User
import kotlin.math.round

class SignUpActivity : BaseActivity() {
    private var binding : ActivitySignUpBinding? = null

    override fun setLayout(): ViewBinding {
        return ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivitySignUpBinding
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()
        binding?.btnSignUp?.setOnClickListener {
            resisterUser()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
            startActivity(Intent(this@SignUpActivity,IntroActivity::class.java))
            finish()
        }
    }
    fun userRegisteredSuccess(){
        hideProgressDialog()
        Toast.makeText(this@SignUpActivity,"You are successfully registered",Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
    private fun resisterUser(){
        val name = binding?.etName?.text.toString().trim(){it <= ' '}
        val email = binding?.etEmail?.text.toString().trim(){it <= ' '}
        val password = binding?.etPassword?.text.toString().trim(){it <= ' '}
        if (validateForm(name,email, password)){
          showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                task ->
                if(task.isSuccessful){
                    val firebaseUser : FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid,name,registeredEmail)
                    FireStore().registerUser(this@SignUpActivity,user)
                }
                else{
                    Toast.makeText(this@SignUpActivity,task.exception!!.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun validateForm(name:String, email: String, password:String): Boolean{
        return when{
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please Enter Your Name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please Enter Your Email")
                false
            }
            TextUtils.isEmpty(password) ->{
                showErrorSnackBar("Please Enter Your Password")
                false
            }
            else -> true
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SignUpActivity,IntroActivity::class.java))
        finish()
        super.onBackPressed()
    }
}