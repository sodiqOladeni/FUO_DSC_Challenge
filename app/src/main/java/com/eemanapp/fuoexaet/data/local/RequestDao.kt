package com.eemanapp.fuoexaet.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eemanapp.fuoexaet.model.Filter
import com.eemanapp.fuoexaet.model.Request
import com.eemanapp.fuoexaet.model.User
import java.util.*
import kotlin.collections.ArrayList

@Dao
interface RequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setRequest(request: Request):Long

    @Update
    fun updateRequest(vararg request: Request)

    @Delete
    fun deleteRequest(vararg request: Request)

    @Query("select * from request order by requestTime DESC")
    fun getRequests():LiveData<List<Request>>

    @Query("select * from request where ((:startDate is null or requestTime >= :startDate) and (:endDate is null or requestTime <= :endDate)) and ((requestType is null or requestType in (:types))) and ((requestStatus is null or requestStatus in (:status))) order by requestTime DESC")
    fun getRequests(startDate: Long?, endDate:Long?, types:ArrayList<String>?, status:ArrayList<String>?):LiveData<List<Request>>

    @Query("delete from request")
    fun deleteRequests():Int

    @Query("select * from request where uniqueId = :userId order by requestTime DESC")
    fun getRequestWithUserId(userId:String):LiveData<List<Request>>

    @Query("select * from request where requestUniqueId = :requestId")
    fun getRequestWithId(requestId:String):LiveData<Request>
}