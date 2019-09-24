package com.eemanapp.fuoexaet.data.local

import androidx.room.TypeConverter
import com.eemanapp.fuoexaet.model.User
import com.google.gson.Gson



class Converters {

    @TypeConverter
    fun fromUserToJson(user: User?):String?{
        return Gson().toJson(user)
    }

    @TypeConverter
    fun fromJsonToUser(userJson: String?):User?{
        return Gson().fromJson(userJson, User::class.java)
    }
}