package com.eemanapp.fuoexaet.view.prep


import android.animation.AnimatorInflater
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.view.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*



/**
 * A simple [Fragment] subclass.
 */
class SplashActivity : AppCompatActivity() {

    private val TIME_OUT = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val fadeAnimator = AnimatorInflater.loadAnimator(this, R.anim.alpha)
        fadeAnimator?.apply {
            setTarget(imageView)
            start()
        }

        val prefManager = SharedPref(this)
        val userId = prefManager.getUserId()

        Handler().postDelayed({
            if (userId.isNullOrEmpty()) {
                startActivity(Intent(this, PrepActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }

            Log.v("SplashActivity", "UserId ==> $userId")
        }, TIME_OUT)
    }
}
