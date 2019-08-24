package com.eemanapp.fuoexaet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eemanapp.fuoexaet.model.User

@Database(entities = [User::class], version = 1)
abstract class FuoDb : RoomDatabase(){
    abstract val userDao:UserDao
    companion object{
        private lateinit var INSTANCE: FuoDb

        fun getDatabase(context: Context): FuoDb {
            synchronized(FuoDb::class.java) {
                if (!Companion::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        FuoDb::class.java,
                        "Fuo_Database").build()
                }
            }
            return INSTANCE
        }
    }
}