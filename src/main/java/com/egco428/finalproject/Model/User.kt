package com.egco428.finalproject.Model

data class User(val name: String, var woodCount: Int,var foodCount:Int ,var hungry:Int,var havePartOfBoat: Array<Boolean>)
{
    constructor(name: String): this(name,0,0,100, arrayOf(false,false,false)) {
    }


}
