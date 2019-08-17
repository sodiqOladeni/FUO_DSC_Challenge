package com.eemanapp.fuoexaet.model

data class User(
    var fullName: String,
    var schoolId: String,
    var email: String,
    var phoneNumber: String,
    var imageUri:String,
    // This is the user type
    // 0 ==> Student
    // 1 ==> Student coordinator
    // 2 ==> Security
    var userWho:Int,
    var uniqueId:String? = null,
    var password: String,
    //Non general information
    //These information only applies to student
    var college: String? = null,
    var dept: String? = null,
    var entryYear: String? = null,
    var hallOfResidence: String? = null
)