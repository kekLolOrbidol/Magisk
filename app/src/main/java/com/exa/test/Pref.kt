package com.exa.test

import android.content.Context
import android.content.SharedPreferences

class Pref(val context: Context) {

    var pref : SharedPreferences? = null

    fun getSp(name : String){
        pref = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun getStr(name : String) : String?{
        return pref?.getString(name, null)
    }

    fun putStr(name : String, value : String){
        pref?.edit()?.putString(name, value)?.apply()
    }
}