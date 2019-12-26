package com.eemanapp.fuoexaet.data.local

import androidx.room.TypeConverter
import com.eemanapp.fuoexaet.model.User
import com.google.gson.Gson
import java.util.*


class Converters {

    @TypeConverter
    fun fromUserToJson(user: User?):String?{
        return Gson().toJson(user)
    }

    @TypeConverter
    fun fromJsonToUser(userJson: String?):User?{
        return Gson().fromJson(userJson, User::class.java)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return (date?.time)
    }
}