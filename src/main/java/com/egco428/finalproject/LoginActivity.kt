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

class LoginActivity : AppCompatActivity() {
    //View Variable
    lateinit var usernameEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginBtn:ImageView
    lateinit var registerTextView:TextView
    //Sound Variable
    lateinit var buttonMediaPlayer: MediaPlayer
    lateinit var backgroundMediaPlayer: MediaPlayer



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Binding View
        usernameEditText = findViewById(R.id.usernameLoginEditText)
        passwordEditText = findViewById(R.id.passwordLoginEditText)
        loginBtn = findViewById(R.id.loginBtn)
        registerTextView = findViewById(R.id.registerTextView)

        //Sound Setting
        buttonMediaPlayer = MediaPlayer.create(this,R.raw.quick_jump)
        backgroundMediaPlayer = MediaPlayer.create(this,R.raw.bg_login)
        backgroundMediaPlayer.start()
        backgroundMediaPlayer.isLooping = true


        //Add onClick Listener to Login Button
        loginBtn.setOnClickListener {
            buttonMediaPlayer.start()

            //Check Username and Password are empty -> Show edittext error
            if (usernameEditText.text.isNullOrEmpty()) {
                usernameEditText.error = "Please enter an username"
            }
            if(passwordEditText.text.isNullOrEmpty()){
                passwordEditText.error = "Please enter a password"

            }

            //If Username and Password are not empty
            if(!passwordEditText.text.isNullOrEmpty() && !usernameEditText.text.isNullOrEmpty()){
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                //Check username and password are match in data file (use UserProvider.authenticatedUser)
                //Return success Boolean
                val success = UserProvider.authenticatedUser(this,username,password)
                if(success){
                    Toast.makeText(this,"Login Success! Welcome to CastAway,"+username, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this,BaseActivity::class.java)
                    intent.putExtra("username",username)

                    passwordEditText.text.clear()

                    startActivity(intent)
                    finish()


                }else{
                    Toast.makeText(this,"Username does not match Password",Toast.LENGTH_SHORT).show()

                }


            }

        }

        // Set onClick Listener to Register Text (for going to RegisterActivity)
        registerTextView.setOnClickListener {
            buttonMediaPlayer.start()
            registerTextView.animate().setDuration(200).alpha(0F).withEndAction(Runnable {
                val intent = Intent(this,RegisterActivity::class.java)
                startActivity(intent)
                registerTextView.alpha = 1.0F //1 สีเข้มเท่าเดิม
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