package com.egco428.finalproject

import android.content.Intent
import android.graphics.PorterDuff
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView


class MainActivity : AppCompatActivity() {
    //View Variable
    lateinit var loginBtn:ImageView
    lateinit var registerBtn:ImageView

    //Sound Variable
    lateinit var buttonMediaPlayer: MediaPlayer
    lateinit var backgroundMediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Binding View
        loginBtn = findViewById(R.id.loginView)
        registerBtn = findViewById(R.id.registerView)

        //Sound Setting
        buttonMediaPlayer = MediaPlayer.create(this,R.raw.quick_jump)
        backgroundMediaPlayer = MediaPlayer.create(this,R.raw.bg_main)

        //Add Login Btn Listener (Go to LoginActivity)
        loginBtn.setOnClickListener {
            buttonMediaPlayer.start()
            loginBtn.animate().setDuration(200).alpha(0F).withEndAction(Runnable {
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
                loginBtn.alpha = 1F

            })
        }


        //Add Register Btn Listener (Go to RegisterActivity)
        registerBtn.setOnClickListener {
            buttonMediaPlayer.start()
            registerBtn.animate().setDuration(200).alpha(0F).withEndAction(Runnable {
                val intent = Intent(this,RegisterActivity::class.java)
                startActivity(intent)
                registerBtn.alpha = 1F

            })
        }


    }

    override fun onPause() {
        super.onPause()
        backgroundMediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        backgroundMediaPlayer.start()
    }

}