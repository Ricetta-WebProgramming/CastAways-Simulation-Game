package com.egco428.finalproject

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.egco428.finalproject.Model.UserProvider

class RegisterActivity : AppCompatActivity() {
    //View Variable
    lateinit var usernameEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var registerBtn : ImageView
    lateinit var loginTextView: TextView

    //Sound Variable
    lateinit var buttonMediaPlayer: MediaPlayer
    lateinit var backgroundMediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Binding View
        usernameEditText = findViewById(R.id.usernameRegisEditText)
        passwordEditText = findViewById(R.id.passwordRegisEditText)
        registerBtn = findViewById(R.id.registerBtn)
        loginTextView = findViewById(R.id.loginTextView)

        //Sound Setting
        buttonMediaPlayer = MediaPlayer.create(this,R.raw.quick_jump)
        backgroundMediaPlayer = MediaPlayer.create(this,R.raw.bg_login)


        //Start loop background sound
        backgroundMediaPlayer.start()
        backgroundMediaPlayer.isLooping = true


        //Add onClick Listener
        registerBtn.setOnClickListener {
            buttonMediaPlayer.start()
            //Check EditText is Empty -> show error edittext
            if (usernameEditText.text.isNullOrEmpty()) {
                usernameEditText.error = "Please enter an username"
            }
            if(passwordEditText.text.isNullOrEmpty()){
                passwordEditText.error = "Please enter a password"

            }

            //If username and password are not empty
            if(!passwordEditText.text.isNullOrEmpty() && !usernameEditText.text.isNullOrEmpty()){
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                // send username to UserProvider for checking duplicate user and create user
                // return Success boolean
                val success = UserProvider.createUser(this,username,password)
                if(success){
                    Toast.makeText(this,"Create Success! Welcome to CastAway,"+username, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this,BaseActivity::class.java)
                    intent.putExtra("username",username)

                    //Clear data in edittext before go to another activity
                    usernameEditText.text.clear()
                    passwordEditText.text.clear()
                    startActivity(intent)
                    finish()


                }else{
                    Toast.makeText(this,"Duplicate Username! Please rename an username", Toast.LENGTH_SHORT).show()

                }
            }


        }

        //OnClick to change activity
        loginTextView.setOnClickListener {
            buttonMediaPlayer.start()


            loginTextView.animate().setDuration(200).alpha(0F).withEndAction(Runnable {
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)

                loginTextView.alpha = 1.0F //1 สีเข้มเท่าเดิม
                finish()

            })


        }

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