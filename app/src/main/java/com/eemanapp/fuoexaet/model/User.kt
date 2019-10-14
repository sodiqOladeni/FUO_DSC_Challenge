package com.eemanapp.fuoexaet.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var firstName: String = "",
    var lastName: String = "",
    var schoolId: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var imageUri: String = "",
    var hasUserPay:Boolean = false,
    var userPaymentRef:String? = null,
    var fcmToken:String? = "",
    // This is the user type
    // 0 ==> Student
    // 1 ==> Student coordinator
    // 2 ==> Security
    var userWho: Int = -1,
    var uniqueId: String? = null,
    var password: String = "",
    var userCreatedByWho: String? = null,
    var userCreatedWhen: Long? = null,
    //Non general information
    //These information only applies to student
    val programme: String = "Bsc",
    var college: String? = null,
    var dept: String? = null,
    var entryYear: String? = null,
    var hallOfResidence: String? = null,
    var hallRoomNumber: String? = null
) : Parcelable {
    val getHallAndRoomNumber: String
        get() = "$hallOfResidence ($hallRoomNumber)"
}