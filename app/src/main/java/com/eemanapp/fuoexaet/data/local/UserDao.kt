package com.eemanapp.fuoexaet.data.local

import androidx.room.*
import com.eemanapp.fuoexaet.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setUser(user: User):Long

    @Query("select * from user")
    fun getUser():User

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(user: User):Int

    @Query("delete from user")
    fun deleteUser():Int
}