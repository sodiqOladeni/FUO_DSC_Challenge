package com.eemanapp.fuoexaet.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "request")
data class Request(
    @PrimaryKey(autoGenerate = true)
    @NonNull var dbId:Long = -1,
    var requestUniqueId:String? = null,
    var requestType:String = "",
    var requestStatus: String? = "",
    var approveCoordinator:String? = null,
    var departureDate:String = "",
    var departureTime:String = "",
    var arrivalDate:String = "",
    var arrivalTime:String = "",
    var location:String = "",
    var purpose:String = "",
    var requestTime:Long = 0L,
    var user:User? = null
) : Parcelable