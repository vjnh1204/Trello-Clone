package com.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.projemanag.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private var binding: ActivityIntroBinding? = null

    override fun setLayout(): ViewBinding = ActivityIntroBinding.inflate(layoutInflater)

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivityIntroBinding
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setUpUI()
    }

    private fun setUpUI(){
        binding?.btnSignUpIntro?.setOnClickListener{
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }
        binding?.btnSignInIntro?.setOnClickListener{
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }
    }
}