package com.eemanapp.fuoexaet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.User

@Database(entities = [Request::class], version = 1)
@TypeConverters(Converters::class)
abstract class FuoDb : RoomDatabase(){
    abstract val requestDao:RequestDao

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