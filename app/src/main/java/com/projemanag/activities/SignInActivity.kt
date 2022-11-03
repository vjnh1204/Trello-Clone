package com.projemanag.activities

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import com.projemanag.databinding.ActivitySignInBinding
import com.projemanag.firebase.FireStore
import com.projemanag.models.User

class SignInActivity : BaseActivity() {
    private var binding : ActivitySignInBinding? = null
    private lateinit var auth: FirebaseAuth
    override fun setLayout(): ViewBinding {
        return ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivitySignInBinding
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()
        auth = FirebaseAuth.getInstance()
        binding?.btnSignIn?.setOnClickListener{
            loginUser()
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun signInSuccess(user: User){
        hideProgressDialog()
        val intent = Intent(this@SignInActivity,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    private fun loginUser(){
        val email = binding?.etEmail?.text.toString().trim(){it <= ' '}
        val password = binding?.etPassword?.text.toString().trim(){it <= ' '}
        if(validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task->
                    if (task.isSuccessful){
                        FireStore().loginUser(this@SignInActivity)
                    }
                    else{
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(email:String, password:String):Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Your Email Is Blank")
                false
            }
            TextUtils.isEmpty(password)-> {
                showErrorSnackBar("Your Password Is Blank")
                false
            }
            else -> true
        }
    }
}