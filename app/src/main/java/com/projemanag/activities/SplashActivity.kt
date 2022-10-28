package com.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.projemanag.databinding.ActivitySplashBinding
import kotlinx.coroutines.*


class SplashActivity : BaseActivity() {
    private var binding : ActivitySplashBinding? = null

    override fun setLayout(): ViewBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun initView(viewBinding: ViewBinding) {
        binding = viewBinding as ActivitySplashBinding
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")

        binding?.tvAppName?.typeface = typeface
        delayScreen()
    }

    private fun delayScreen(){
        lifecycleScope.launch {
            delay(3000)
            withContext(Dispatchers.Main){
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
        }
    }
}