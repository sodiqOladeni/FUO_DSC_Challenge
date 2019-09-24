package com.eemanapp.fuoexaet.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    @NonNull var dbId:Long = -1,
    var firstName: String = "",
    var lastName: String = "",
    var schoolId: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var imageUri:String = "",
    // This is the user type
    // 0 ==> Student
    // 1 ==> Student coordinator
    // 2 ==> Security
    var userWho:Int = -1,
    var uniqueId:String? = null,
    var password: String = "",
    //Non general information
    //These information only applies to student
    var programme:String = "Bsc",
    var college: String? = null,
    var dept: String? = null,
    var entryYear: String? = null,
    var hallOfResidence: String? = null
) : Parcelable