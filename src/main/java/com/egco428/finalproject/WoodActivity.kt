package com.egco428.finalproject

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import com.egco428.finalproject.Model.User
import com.egco428.finalproject.Model.UserProvider

class WoodActivity : AppCompatActivity() , GestureDetector.OnGestureListener , SensorEventListener {
    //View Variable
    lateinit var itemView:ImageView
    lateinit var woodText:TextView
    lateinit var backBtn:Button
    lateinit var appleText:TextView


    //Boolean for tell item type
    // wood = true , apple = false
    private var isWood = true

    //Sound Variable
    lateinit var woodMediaPlayer :MediaPlayer
    lateinit var pickMediaPlayer :MediaPlayer
    lateinit var branchMediaPlayer :MediaPlayer
    lateinit var buttonMediaPlayer :MediaPlayer
    lateinit var backgroundMediaPlayer: MediaPlayer



    //User Variable
    lateinit var user: User

    //Shake Device Variable
    private var sensorManager: SensorManager? = null
    private var lastUpdate: Long = 0


    //Swipe Screen Variable
    private val MIN_SWIPE_DISTANCE_X = 100
    private val MAX_SWIPE_DISTANCE_X = 1000
    private var gestureDetector: GestureDetectorCompat? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wood)

        //Binding View
        itemView = findViewById(R.id.itemView)
        woodText = findViewById(R.id.woodText)
        backBtn = findViewById(R.id.backBtn)
        appleText = findViewById(R.id.appleText)

        //Sound Setting
        woodMediaPlayer = MediaPlayer.create(this,R.raw.axe)
        pickMediaPlayer = MediaPlayer.create(this,R.raw.extra_bonus)
        buttonMediaPlayer = MediaPlayer.create(this,R.raw.quick_jump)
        branchMediaPlayer = MediaPlayer.create(this,R.raw.branchshake)
        backgroundMediaPlayer = MediaPlayer.create(this,R.raw.bg_wood)

        //Start Loop Background Sound
        backgroundMediaPlayer.start()
        backgroundMediaPlayer.isLooping = true


        //Get Bundle from Base Page
        val bundle = intent.extras
        if(bundle!=null){
            val username = bundle.getString("username")
            user = UserProvider.getUserData(this,username.toString())
            Log.d("UserWood",user.woodCount.toString())
            Log.d("Bundle",user.name.toString())


        }

        //Prepare Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lastUpdate = System.currentTimeMillis()
        this.gestureDetector = GestureDetectorCompat(this, this)


        // Add onClick Listener

        //Back Button - Save user's status and Back to BaseActivity
        backBtn.setOnClickListener {
            UserProvider.saveUser(this,user)
            buttonMediaPlayer.start()
            val intent = Intent(this,BaseActivity::class.java)
            intent.putExtra("username",user.name)
            backgroundMediaPlayer.stop()
            startActivity(intent)
            finish()
        }


        //Item Button - When user takes action (shake or swipe), View is visible.
        //When user click -> View = invisible and increase user's status
        itemView.setOnClickListener {
            itemView.visibility = View.INVISIBLE
            pickMediaPlayer.start()

            // if user swipe -> woodCount
            if(isWood){
                user.woodCount++
                woodText.text = "Wood x %3d".format(user.woodCount)

                Toast.makeText(this,"Receive a wood!",Toast.LENGTH_SHORT).show()
                Log.d("Wood",user.woodCount.toString())
            }

            // if user shake -> foodCount
            else{
                user.foodCount++
                appleText.text="Food x %3d".format(user.foodCount)
                Toast.makeText(this,"Receive a food!",Toast.LENGTH_SHORT).show()

            }
        }


        // Show user's status on screen
        woodText.text = "Wood x %3d".format(user.woodCount)
        appleText.text="Food x %3d".format(user.foodCount)


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.gestureDetector!!.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onFling(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        //Get Swipe in x-axis
        val deltaX = event1.x - event2.x
        val deltaAbsX = Math.abs(event1.x - event2.x)
        if((deltaAbsX>=MIN_SWIPE_DISTANCE_X)&&(deltaAbsX<=MAX_SWIPE_DISTANCE_X)){
            if(deltaX>0){
                // "Swipe to Left"
                generateItem(true)
            }
            else{
                // "Swipe to Right"
                generateItem(true)

            }
        }
        return true
    }

    //Generate Item Function -> receive boolean (wood/swipe  or  food/shake)
    private fun generateItem(wood:Boolean) {
        var itemName=""
        isWood = wood
        if(wood){
            itemName="wood"
            woodMediaPlayer.start()
        }
        else{
            itemName="apple"
            branchMediaPlayer.start()
        }

        //Change ImageView by type
        val drawableID = this.resources.getIdentifier(itemName,"drawable",this.packageName)
        itemView.setImageResource(drawableID)
        itemView.alpha = 0.0F
        itemView.visibility = View.VISIBLE
        itemView.animate().setDuration(200).alpha(1.0F)

    }


    //-------------------------------- Sensor Set -------------------------------------------------
    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event)
        }    }

    private fun getAccelerometer(event: SensorEvent) {
        val values = event.values
        val x = values[0]
        val y = values[1]
        val z = values[2]

        val accel =
            (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)
        val actualTime = System.currentTimeMillis()

        if (accel >= 2) {
            if (actualTime - lastUpdate < 1000) {
                return
            }

            // Real shake
            lastUpdate = actualTime
            generateItem(false)

        }
    }


    //-------------- OnResume & OnPause -----------------------
    override fun onPause() {
        super.onPause()
        // Close Sensor
        sensorManager!!.unregisterListener(this)
        backgroundMediaPlayer.stop()
    }

    override fun onResume() {
        // Open Sensor
        super.onResume()
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        backgroundMediaPlayer.start()

    }





    //-------------- Not Use ----------------------------
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
    }






}