package com.eemanapp.fuoexaet.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.eemanapp.fuoexaet.utils.Constants

class SharedPref(var context:Context) {
    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    fun setUserId(id:String){
        pref.edit().apply {
            putString(Constants.USER_ID, id)
            apply()
        }
    }

    fun getUserId():String?{
        return pref.getString(Constants.USER_ID, "")
    }

    fun deletePrefs(){
        pref.edit().apply {
            clear()
            apply()
        }
    }
}