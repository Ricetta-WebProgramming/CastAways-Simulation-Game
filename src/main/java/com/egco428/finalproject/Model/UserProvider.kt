package com.egco428.finalproject.Model

import android.content.Context
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

object UserProvider {
    private val filename = "users.txt"


    fun createUser(context: Context, username:String, password:String):Boolean{
       val notDuplicated =  checkDuplicateUser(context,username)
        if(notDuplicated){
            //ชื่อไม่ซ้ำเลย -> สร้างได้
            try {

                val fOut = context.openFileOutput(filename, Context.MODE_APPEND)
                val data = username+","+password+","+"0"+","+"0"+","+"100"+","+"false"+","+"false"+","+"false"+"\n"
                fOut.write(data.toByteArray())
                fOut.close()
                return true

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        return false

    }


    //Check ว่าชื่อผู้ใช้ซ้ำกันในไฟล์หรือไม่
    fun checkDuplicateUser(context: Context, username: String): Boolean {
        try{
            val fIn = context.openFileInput(filename)
            val mfile = InputStreamReader(fIn)
            val br = BufferedReader(mfile)
            var line = br.readLine()
            while (line != null) {
                var raw = line.split(",")
                if(raw[0]==username ){
                    //Username Duplicate!
                    return false
                }
                line = br.readLine()

            }


            br.close()
            mfile.close()


        }catch (e:Exception){
            e.printStackTrace()
        }


        return true

    }


    //Check ว่า Username & Password ตรงกันหรือไม่
    fun authenticatedUser(context: Context, username:String, password:String):Boolean{
        try{
            val fIn = context.openFileInput(filename)
            val mfile = InputStreamReader(fIn)
            val br = BufferedReader(mfile)
            var line = br.readLine()
            while (line != null) {
                var raw = line.split(",")
                if(raw[0]==username && raw[1]==password){
                    //Correct Username & Password
                    return true
                }
                line = br.readLine()

            }


            br.close()
            mfile.close()


        }catch (e:Exception){
            e.printStackTrace()
        }

        return false


    }


    //ดึงข้อมูลมาเก็บในตัวแปร User
    fun getUserData(context: Context,username: String):User{
        var tempUser:User =User(username)
        try{
            val fIn = context.openFileInput(filename)
            val mfile = InputStreamReader(fIn)
            val br = BufferedReader(mfile)
            var line = br.readLine()
            while (line != null) {
                var raw = line.split(",")
                if(raw[0]==username ){
                    //Found User Record
                    tempUser = User(username,raw[2].toInt(),raw[3].toInt(),raw[4].toInt(), arrayOf(raw[5].toBoolean(),raw[6].toBoolean(),raw[7].toBoolean()))
                }
                line = br.readLine()

            }


            br.close()
            mfile.close()


        }catch (e:Exception){
            e.printStackTrace()
        }

        return tempUser!!

    }



    fun saveUser(context: Context,user: User){
        val username = user.name
        val woodCount = user.woodCount
        val foodCount = user.foodCount
        val hungryValue = user.hungry
        val havePart = user.havePartOfBoat
        var password =""
        var dataSet =""
        try{


            val fIn = context.openFileInput(filename)
            val mfile = InputStreamReader(fIn)
            val br = BufferedReader(mfile)
            var line = br.readLine()
            while (line != null) {
                var raw = line.split(",")


                if(raw[0]==username ){
                    //Found User!
                    password = raw[1].toString()

                }else{
                    dataSet = dataSet+line+"\n"

                }
                line = br.readLine()

            }

            br.close()
            mfile.close()

            var fOut = context.openFileOutput(filename, Context.MODE_PRIVATE)
            val save = username+","+password+","+woodCount+","+foodCount+","+hungryValue+","+havePart[0]+","+havePart[1]+","+havePart[2]+"\n"
            dataSet = dataSet+save
            fOut.write(dataSet.toByteArray())
            fOut.close()




        }catch (e:Exception){
            e.printStackTrace()
        }




    }


    fun getAllUser(context: Context):MutableList<User>{
        val allUser :MutableList<User> = mutableListOf()
        try{
            val fIn = context.openFileInput(filename)
            val mfile = InputStreamReader(fIn)
            val br = BufferedReader(mfile)
            var line = br.readLine()
            while (line != null) {

                var raw = line.split(",")
                val tempUser = User(raw[0],raw[2].toInt(),raw[3].toInt(),raw[4].toInt(), arrayOf(raw[5].toBoolean(),raw[6].toBoolean(),raw[7].toBoolean()))
                allUser.add(tempUser)
                line = br.readLine()

            }


            br.close()
            mfile.close()


        }catch (e:Exception){
            e.printStackTrace()
        }

        return allUser

    }





}