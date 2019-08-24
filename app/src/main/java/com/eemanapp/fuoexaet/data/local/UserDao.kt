package com.eemanapp.fuoexaet.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.eemanapp.fuoexaet.model.User

@Dao
interface UserDao {

    @Insert
    fun setUser(user: User):Long

    @Query("select * from user")
    fun getUser():User

    @Query("delete from user")
    fun deleteUser():Int
}