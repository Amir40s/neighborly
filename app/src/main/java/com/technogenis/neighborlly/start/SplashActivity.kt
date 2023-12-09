package com.technogenis.neighborlly.start

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.technogenis.neighborlly.R
import android.os.Handler
import android.os.Looper
import com.technogenis.neighborlly.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private lateinit var fadeAdmin : Animation
    private lateinit var bottomSlide : Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fadeAdmin = AnimationUtils.loadAnimation(this, R.anim.fade_anim)
        bottomSlide = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)

        binding.logoCard.startAnimation(fadeAdmin)
        binding.titleText.startAnimation(bottomSlide)

        // timer for delay 3 sec then auto move to next screen
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        },3000)
    }
}