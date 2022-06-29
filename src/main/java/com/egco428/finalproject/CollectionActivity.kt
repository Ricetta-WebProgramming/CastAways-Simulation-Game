package com.egco428.finalproject

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.egco428.finalproject.Adapter.ItemOnClickListener
import com.egco428.finalproject.Adapter.UserAdapter
import com.egco428.finalproject.Model.User
import com.egco428.finalproject.Model.UserProvider
import org.w3c.dom.Text

class CollectionActivity : AppCompatActivity() , ItemOnClickListener {

    //View Variable
    lateinit var backBtn:Button
    lateinit var woodText:TextView
    lateinit var foodText:TextView
    lateinit var recyclerView: RecyclerView

    //Sound Variable
    lateinit var buttonMediaPlayer: MediaPlayer
    lateinit var backgroundMediaPlayer: MediaPlayer


    //List Variable
    private var userList : MutableList<User> = mutableListOf()
    private var adapter: UserAdapter? = null


    //User Variable
    private var user:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        //Get Bundle from Base Page
        val bundle = intent.extras
        if(bundle!=null){
            val username = bundle.getString("username")
            user = UserProvider.getUserData(this,username.toString())

        }


        //Binding
        backBtn = findViewById(R.id.backCollectionBtn)
        woodText = findViewById(R.id.woodCountText)
        foodText = findViewById(R.id.foodCountText)
        recyclerView = findViewById(R.id.recyclerList)


        //Sound Setting
        buttonMediaPlayer = MediaPlayer.create(this,R.raw.quick_jump)
        backgroundMediaPlayer = MediaPlayer.create(this,R.raw.bg_wood)

        // Set RecyclerView (with Adapter)
        val linearLayoutManager =
            LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        userList = UserProvider.getAllUser(this)
        adapter = UserAdapter(userList, baseContext, this)
        recyclerView.adapter = adapter
        adapter!!.notifyDataSetChanged()
        Log.d("Tag", "Load to Adapter")

        //Set Text for showing status
        woodText.text ="x "+user!!.woodCount.toString()
        foodText.text ="x "+user!!.foodCount.toString()

        //Set onClick -> Back to BaseActivity
        backBtn.setOnClickListener {
            buttonMediaPlayer.start()
            val intent = Intent(this,BaseActivity::class.java)
            intent.putExtra("username",user!!.name)
            startActivity(intent)
            finish()

        }



    }

    override fun onLongClick(position: Int) {
        buttonMediaPlayer.start()
        Toast.makeText(this, "Wood : " + userList[position].woodCount.toString()+", Food : "+userList[position].foodCount.toString(), Toast.LENGTH_SHORT).show()

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