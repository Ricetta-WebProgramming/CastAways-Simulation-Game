package com.egco428.finalproject

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.egco428.finalproject.Model.User
import com.egco428.finalproject.Model.UserProvider
import com.egco428.finalproject.Model.WeatherData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import pl.droidsonroids.gif.GifImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BaseActivity : AppCompatActivity() {
    // View Variable
    lateinit var backgroundView: ImageView
    lateinit var characterView: GifImageView
    lateinit var clockTextView: TextClock
    lateinit var weatherIconView:ImageView
    lateinit var repairBtn:ImageView
    lateinit var cutwoodBtn:ImageView
    lateinit var bagView: ImageView
    lateinit var gifLoading: GifImageView
    lateinit var progressBar:ProgressBar

    //Sound Variable
    lateinit var buttonMediaPlayer: MediaPlayer
    lateinit var backgroundMediaPlayer: MediaPlayer
    lateinit var appleMediaPlayer: MediaPlayer


    //Counter Variable
    lateinit var countTime:CountDownTimer

    //Hungry Config
    private val FOOD_INCREASE = 5   //Eat food Increase __ point
    private val HUNGRY_INCREASE = 2 //Every 10 sec Decrease __ point


    //User Variable
    lateinit var user: User
    private  var username= ""
    private var isSafe = true  //Weather is safe for go into wood or repair a boat

    // Move Character Limit - ป้องกันการตกแมพ
    private var countLeft = 0
    private var countRight = 0
    private var countUp = 0
    private var countDown = 0
    private val MAXMOVELEFT = 2
    private val MAXMOVERIGHT = 2
    private val MAXMOVEUP = 1
    private val MAXMOVEDOWN = 1

    //RequestData from API & Location
    private var weatherURL = ""
    private val apiKey = "f6419da150930b11e4bf57d0eb9d0023"
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var lastLocation: LatLng? = null
    lateinit var weatherData:WeatherData
    private var lastWeatherType =""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        //Get Bundle from Register & Login Page
        val bundle = intent.extras
        if(bundle!=null){
            username = bundle.getString("username").toString()
            user = UserProvider.getUserData(this,username.toString())
            Log.d("Bundle",user.name.toString())


        }

        // Binding View
        characterView = findViewById(R.id.characterView)
        backgroundView = findViewById(R.id.backgroundView)
        clockTextView = findViewById(R.id.clockTextView)
        weatherIconView = findViewById(R.id.weatherIconView)
        repairBtn = findViewById(R.id.repairView)
        cutwoodBtn = findViewById(R.id.cutwoodView)
        bagView = findViewById(R.id.bagView)
        gifLoading = findViewById(R.id.gifLoadImageView)
        progressBar = findViewById(R.id.progressBar)

        //Sound Setting
        buttonMediaPlayer = MediaPlayer.create(this,R.raw.quick_jump)
        backgroundMediaPlayer = MediaPlayer.create(this,R.raw.bg_main)
        appleMediaPlayer = MediaPlayer.create(this,R.raw.apple_bite)

        backgroundMediaPlayer.isLooping = true
        backgroundMediaPlayer.start()


        //Current Location Service
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                lastLocation = LatLng(location.latitude, location.longitude)
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }

        //Request gps
        requestLocation()

        //Set Progress Bar
        progressBar.max= 100
        progressBar.progress = user.hungry



       // Set Counter for Check Weather , Location , Decrease Hungry value
        countTime = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("CountTime","OnTick - "+millisUntilFinished.toString())
            }
            override fun onFinish() {
                //Invisible loading gif after first time call weather api
                if(gifLoading.visibility==View.VISIBLE){
                    gifLoading.visibility = View.GONE

                }

                //Easy for user -> When weather is not friendly to go into the wood (collect food) == hungry value not decrease
                if(isSafe){
                    user.hungry=user.hungry-HUNGRY_INCREASE
                }
                progressBar.progress = user.hungry

                //If hungry value == 0 -> GameOver
                if(user.hungry<=0){
                    // Game Over
                    gameOver()
                }
                Log.d("CountTime","OnFinish")

                //Call current Location and Weather Api
                requestLocation()
                requestWeather()


                //Start Counter again
                this.start()
            }
        }



        // Add onClick Listener to Button

        //Click at CharacterView -> Eat food -> Increase Hungry value and Decrease foodCount
        characterView.setOnClickListener {
            if( user.foodCount>0){
                appleMediaPlayer.start()
                user.foodCount--
                user.hungry=user.hungry+FOOD_INCREASE
                progressBar.progress = user.hungry
                Toast.makeText(this,"Already eat apple!",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Not Enough apple",Toast.LENGTH_SHORT).show()

            }


        }

        //Go to CollectionActivitys
        bagView.setOnClickListener {
            buttonMediaPlayer.start()
            val intent = Intent(this,CollectionActivity::class.java)
            intent.putExtra("username",user.name)
            startActivity(intent)

        }

        //Go to RepairActivity (only weather is safe)
        repairBtn.setOnClickListener {
            buttonMediaPlayer.start()
            if(isSafe){
                val intent = Intent(this,RepairActivity::class.java)
                intent.putExtra("username",user.name)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Too Dangerous to repair the boat",Toast.LENGTH_SHORT).show()
            }

        }

        //Go to WoodActivity (only weather is safe)
        cutwoodBtn.setOnClickListener {
            buttonMediaPlayer.start()
            if(isSafe){
                val intent = Intent(this,WoodActivity::class.java)
                intent.putExtra("username",user.name)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Too Dangerous to go into the wood",Toast.LENGTH_SHORT).show()

            }

        }


    }

    //GameOver Function
    private fun gameOver() {
        // Set Game Over Dialog & Button in dialog
        val dialog = AlertDialog.Builder(this).setView(R.layout.custom_alert_dialog).show()
        val backBtn = dialog.findViewById<ImageView>(R.id.backMenuBtn)

        // Reset User's status -> Save to data file -> Go to MainActivity
        backBtn!!.setOnClickListener {
            dialog.dismiss()
            user.woodCount = 0
            user.foodCount = 0
            user.hungry = 100
            user.havePartOfBoat = arrayOf(false,false,false)
            UserProvider.saveUser(this,user)

            val intent = Intent(getApplicationContext(),MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    // Request Location from Device
    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 10
                )
            }
            return
        }
        locationManager!!.requestLocationUpdates("gps", 5000, 0F, locationListener!!)

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            10->requestLocation()
            else ->{
                Log.d("GPS Permission","Permission is not granted")
            }
        }
    }



    // Animate Character (move in screen) -> Condition : Weather is safe
    private fun moveCharacter() {
        //true = left , false = right
        characterView.setImageResource(R.drawable.character01)
        var directionX =  kotlin.random.Random.nextBoolean()

        //true = up , false = down
        var directionY = kotlin.random.Random.nextBoolean()

        //---------------- Set X ---------------------
        if(directionX==true){
            // ห้ามวิ่งไปทางซ้ายเกิน 2 ครั้ง (มิเช่นนั้นจะวิ่งขวาแทน)
            if(countLeft<MAXMOVELEFT){
                characterView.animate().setDuration(800).translationXBy(-100F)
                countLeft++
                countRight--
            }
            else{
                characterView.animate().setDuration(800).translationXBy(+100F)
                countLeft--
                countRight++

            }

        }else{
            // ห้ามวิ่งไปทางขวาเกิน 2 ครั้ง (มิเช่นนั้นจะวิ่งซ้ายแทน)
            if(countRight<MAXMOVERIGHT){
                characterView.animate().setDuration(800).translationXBy(+100F)
                countLeft--
                countRight++
            }
            else{
                characterView.animate().setDuration(800).translationXBy(-100F)
                countLeft++
                countRight--

            }

        }

        //---------------- Set Y ---------------------
        if(directionY==true){

            // ห้ามวิ่งไปบนเกิน 1 ครั้ง (มิเช่นนั้นจะวิ่งล่างแทน)
            if(countUp<MAXMOVEUP){
                characterView.animate().setDuration(800).translationYBy(-100F)
                countUp++
                countDown--
            }
            else{
                characterView.animate().setDuration(800).translationYBy(+100F)
                countUp--
                countDown++

            }

        }else{
            // ห้ามวิ่งไปล่างเกิน 1 ครั้ง (มิเช่นนั้นจะวิ่งบนแทน)
            if(countDown<MAXMOVEDOWN){
                characterView.animate().setDuration(800).translationYBy(+100F)
                countUp--
                countDown++
            }
            else{
                characterView.animate().setDuration(800).translationYBy(-100F)
                countUp++
                countDown--

            }

        }


    }


    //Request Weather -> from request from openWeather
    private fun requestWeather(){
        //Request Data JSON
        weatherURL = "https://api.openweathermap.org/data/2.5/weather?lat="+lastLocation!!.latitude.toString()+"&lon="+lastLocation!!.longitude.toString()+"&appid="+apiKey
        Log.d("Weather",weatherURL)
        val client = OkHttpClient()
        var asynceTask = object : AsyncTask<String, String, String>() {

            override fun onPreExecute() {
                // Before Receive Data

            }

            override fun doInBackground(vararg arg: String?): String {
                var builder = Request.Builder()
                builder.url(arg[0].toString())
                val request = builder.build()


                try {
                    val response = client.newCall(request).execute()
                    return response.body!!.string()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return ""
            }

            override fun onPostExecute(result: String?) {

                weatherData = Gson().fromJson(result, WeatherData::class.java)
                Log.d("Weather",weatherData.weather[0].description)
                //Weather Function (Character Action , BackgroundChange,IconChange)
                changeWeather()



            }

        }
        asynceTask.execute(weatherURL)
    }


    override fun onPause() {
        super.onPause()
        locationManager!!.removeUpdates(locationListener!!)
        UserProvider.saveUser(this,user)
        countTime.cancel()
        backgroundMediaPlayer.pause()

    }

    override fun onResume() {
        super.onResume()
        requestLocation()
        user = UserProvider.getUserData(this,username)
        countTime.start()
        backgroundMediaPlayer.start()
    }


    //Weather Function
    private fun changeWeather(){
        val weatherID = weatherData.weather[0].id.toString()
        val currentTime = clockTextView.text.toString().split(":")    // currentTime[0] = Hour , currentTime[1] = Minute
        var timeType = ""
        var weatherType=""
        var drawableName = ""
        var iconName=""

        // Time Condition
        // currentTime[0] = Hour (XX) , currentTime[1] = Minute (XX)
        // 06-15 Day
        if(currentTime[0].toInt()>=6 && currentTime[0].toInt()<=15){
            timeType = "day"
        }
        // 16-18 Evening
        else if(currentTime[0].toInt()>=16 && currentTime[0].toInt()<=18){
            timeType = "evening"
        }
        // 19-05 Night
        else{
            timeType = "night"


        }


        //Weather Condition
        // 7 Group (like weather api icon group)
        // 2XX - Thunderstorm
        if(weatherID.get(0)=='2'){
            weatherType = "storm"
            isSafe = false
            if(lastWeatherType!=weatherType){
                backgroundMediaPlayer.pause()
                backgroundMediaPlayer.reset()
                backgroundMediaPlayer = MediaPlayer.create(this,R.raw.storm)

            }
        }
        // 3XX - Drizzle
        else if(weatherID.get(0)=='3'){
            weatherType = "drizzle"
            isSafe = true
            if(lastWeatherType!=weatherType){
                backgroundMediaPlayer.pause()
                backgroundMediaPlayer.reset()
                backgroundMediaPlayer = MediaPlayer.create(this,R.raw.drizzle)

            }

        }
        // 5XX - Rain
        else if(weatherID.get(0)=='5'){
            weatherType = "rain"
            isSafe = false
            if(lastWeatherType!=weatherType){
                backgroundMediaPlayer.pause()
                backgroundMediaPlayer.reset()
                backgroundMediaPlayer = MediaPlayer.create(this,R.raw.rain)
            }

        }// 6XX - Snow
        else if(weatherID.get(0)=='6'){
            weatherType = "snow"
            isSafe = true
            if(lastWeatherType!=weatherType){
                backgroundMediaPlayer.pause()
                backgroundMediaPlayer.reset()
                backgroundMediaPlayer = MediaPlayer.create(this,R.raw.snow)
            }

        }// 7XX - Atmosphere
        else if(weatherID.get(0)=='7'){
            weatherType = "atmosphere"
            isSafe = false
            if(lastWeatherType!=weatherType){
                backgroundMediaPlayer.pause()
                backgroundMediaPlayer.reset()
                backgroundMediaPlayer = MediaPlayer.create(this,R.raw.atmosphere)
            }

        }// 800 - Clear , 80X - Clounds
        else {
            //Clear
            if(weatherID.get(2)=='0'){
                weatherType = "clear"
                isSafe = true
                if(lastWeatherType!=weatherType){
                    backgroundMediaPlayer.pause()
                    backgroundMediaPlayer.reset()
                    backgroundMediaPlayer = MediaPlayer.create(this,R.raw.clear)
                }

            }
            //Cloud
            else{
                weatherType = "cloud"
                isSafe = true
                if(lastWeatherType!=weatherType){
                    backgroundMediaPlayer.pause()
                    backgroundMediaPlayer.reset()
                    backgroundMediaPlayer = MediaPlayer.create(this,R.raw.cloud)
                }

            }

        }

        backgroundMediaPlayer.start()
        Log.d("Background","Time : "+timeType+" , Weather :"+weatherType)

        // Assign DrawableID & Icon ID
        drawableName = timeType+"_"+weatherType
        iconName = "icon"+weatherData.weather[0].icon
        val drawableID = this.resources.getIdentifier(drawableName,"drawable",this.packageName)

        //ตั้งค่าตามสภาพอากาศ
        backgroundView.setImageResource(drawableID)
        val iconID = this.resources.getIdentifier(iconName,"drawable",this.packageName)
        weatherIconView.setImageResource(iconID)

        //Selection Character Action Function
        selectCharacterAction()

        lastWeatherType = weatherType



    }


    private fun selectCharacterAction() {
        //Weather is Safe
        if(isSafe){
            moveCharacter()

        }else{
            restCharacter()

        }
    }


    //Character action -> Conditon : Weather is not save
    private fun restCharacter() {
        characterView.setImageResource(R.drawable.character02)

    }


}



