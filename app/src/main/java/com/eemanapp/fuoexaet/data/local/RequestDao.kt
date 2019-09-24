package com.eemanapp.fuoexaet.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.User

@Dao
interface RequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setRequest(request: Request):Long

    @Update
    fun updateRequest(vararg request: Request)

    @Query("select * from request")
    fun getRequests():LiveData<List<Request>>

    @Query("delete from request")
    fun deleteRequests():Int
}