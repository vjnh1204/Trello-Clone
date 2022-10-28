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
    private fun loginUser(){
        val email = binding?.etEmail?.text.toString().trim(){it <= ' '}
        val password = binding?.etPassword?.text.toString().trim(){it <= ' '}
        if(validateForm(email,password)){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth.currentUser
                    startActivity(Intent(this@SignInActivity,MainActivity::class.java))

                } else {
                    // If sign in fails, display a message to the user.
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