package com.egco428.finalproject

import android.content.Intent
import android.media.Image
import android.media.MediaPlayer
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.egco428.finalproject.Model.User
import com.egco428.finalproject.Model.UserProvider

class RepairActivity : AppCompatActivity() {
    //View Variable
    lateinit var bgView:ImageView
    lateinit var headCraftBtn:ImageView
    lateinit var midCraftBtn:ImageView
    lateinit var tailCraftBtn:ImageView
    lateinit var homeBtn:ImageView
    lateinit var woodText:TextView
    lateinit var leaveBtn: Button

    //Sound Variable
    lateinit var repairMediaPlayer: MediaPlayer
    lateinit var buttonMediaPlayer: MediaPlayer
    lateinit var backgroundMediaPlayer: MediaPlayer

    //User Variable
    private var username=""
    lateinit var user: User


    //Configuration
    private val woodRequire = 5


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repair)
        //Binding View
        bgView = findViewById(R.id.bgRepairView)
        headCraftBtn = findViewById(R.id.headCraftBtn)
        midCraftBtn = findViewById(R.id.midCraftBtn)
        tailCraftBtn = findViewById(R.id.tailCraftBtn)
        homeBtn = findViewById(R.id.homeBtn)
        woodText = findViewById(R.id.woodRemianText)
        leaveBtn = findViewById(R.id.leaveBtn)

        //Sound Setting
        repairMediaPlayer = MediaPlayer.create(this,R.raw.nail)
        buttonMediaPlayer = MediaPlayer.create(this,R.raw.quick_jump)
        backgroundMediaPlayer = MediaPlayer.create(this,R.raw.bg_wood)


        //Start loop background Sound
        backgroundMediaPlayer.start()
        backgroundMediaPlayer.isLooping = true


        //Get Bundle from Base Page
        val bundle = intent.extras
        if(bundle!=null){
            username = bundle.getString("username").toString()
            user = UserProvider.getUserData(this,username.toString())
            Log.d("Bundle",user.name.toString())

        }


        // Add onClick Listener to three part of boat repair
        // exchangeWood function -> receive index of boat parts
        headCraftBtn.setOnClickListener {
            exchangeWood(0,headCraftBtn)
        }

        midCraftBtn.setOnClickListener {
            exchangeWood(1,midCraftBtn)
        }
        tailCraftBtn.setOnClickListener {
            exchangeWood(2,tailCraftBtn)
        }



        // Add onClick -> return to BaseActivity
        homeBtn.setOnClickListener {
            buttonMediaPlayer.start()
            val intent = Intent(this,BaseActivity::class.java)
            intent.putExtra("username",user.name)
            startActivity(intent)
            finish()
        }

        // Add onClick -> check condition for go to ending game function
        leaveBtn.setOnClickListener {
            buttonMediaPlayer.start()
            checkBoatCondition()
        }

        // Show user's wood status
        woodText.text = "Wood x %3d".format(user.woodCount)

    }



    // exchange wood to part of boat function
    private fun exchangeWood(number:Int,button:ImageView) {

        // ยังไม่เคยแลกมาก่อน
        if(user.havePartOfBoat[number]==false){
            // มีไม้มากกว่าหรือเท่ากับที่ต้องการ -> หักจำนวนไม้และปรับค่า boolean เป็น true (ส่วนเรือถูกซ่อมแล้ว)
            if(user.woodCount>=woodRequire){
                repairMediaPlayer.start()
                user.woodCount = user.woodCount-woodRequire
                user.havePartOfBoat[number] = true

                UserProvider.saveUser(this,user)
                woodText.text = "Wood x %3d".format(user.woodCount)
                Toast.makeText(this,"Repair Success!",Toast.LENGTH_SHORT).show()


            }else{
                Toast.makeText(this,"Not Enough Wood",Toast.LENGTH_SHORT).show()

            }

        }
        else{
            Toast.makeText(this,"Already success.",Toast.LENGTH_SHORT).show()
        }

    }


    // check condition for going to ending page
    private fun checkBoatCondition() {
        // if all of part are repaired -> Show Alert Dialog (if user choose "Yes" -> reset user's status and go to LeaveActivity)
        if(user.havePartOfBoat[0] &&user.havePartOfBoat[1] &&user.havePartOfBoat[2]){
            val dialog = AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("If you leave the island, you must throw everything in your bag away")
                .setPositiveButton("Go!",null)
                .setNegativeButton("Back",null)
                .show()

            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                //Reset User Status and save on data file
                user.woodCount = 0
                user.foodCount = 0
                user.havePartOfBoat = arrayOf(false,false,false)
                UserProvider.saveUser(this,user)

                val intent = Intent(this,LeaveActivity::class.java)
                startActivity(intent)
                finish()
            }

            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setOnClickListener {
                dialog.dismiss()
            }


        }else{
            Toast.makeText(this,"Your boat is not ready yet.",Toast.LENGTH_SHORT).show()
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