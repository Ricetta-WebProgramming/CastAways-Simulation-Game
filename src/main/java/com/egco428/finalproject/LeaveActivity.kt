package com.egco428.finalproject

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView

class LeaveActivity : AppCompatActivity() {
    // View Variable
    lateinit var buttonView:ImageView
    lateinit var shipView:ImageView
    lateinit var boxView:ImageView
    //Sound Variable
    lateinit var buttonMediaPlayer: MediaPlayer
    lateinit var backgroundMediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave)

        //Binding View
        buttonView = findViewById(R.id.buttonView)
        shipView = findViewById<ImageView>(R.id.shipView)
        boxView = findViewById<ImageView>(R.id.boxView)

        //Sound Setting
        buttonMediaPlayer = MediaPlayer.create(this,R.raw.quick_jump)
        backgroundMediaPlayer = MediaPlayer.create(this,R.raw.bg_main)


        //--------- Back to main page --------//
        buttonView.setOnClickListener {
            buttonMediaPlayer.start()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //------------- Animation 0f box&button , must have file zoom_in.xml--------------//
        val animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        boxView.startAnimation(animation)
        buttonView.startAnimation(animation)

        //------------- animation of ship -----------//
        val animations = arrayOf(30f, -30f).map { translation ->
            ObjectAnimator.ofFloat(shipView, "translationY", translation).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
        }
        val set = AnimatorSet()
        set.playTogether(animations)
        set.start()


    }

    override fun onPause() {
        super.onPause()
        backgroundMediaPlayer.stop()
    }

    override fun onResume() {
        super.onResume()
        backgroundMediaPlayer.start()
    }
}