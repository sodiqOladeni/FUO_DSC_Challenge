package com.eemanapp.fuoexaet.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants

class SharedPref(var context:Context) {
    private val pref = PreferenceManager.getDefaultSharedPreferences(context)

    fun setUserId(id:String){
        pref.edit().apply {
            putString(Constants.USER_ID, id)
            apply()
        }
    }

    fun getUserId():String?{
        return pref.getString(Constants.USER_ID, "")
    }

    fun setToken(token:String){
        pref.edit().apply {
            putString(Constants.FCM_TOKEN, token)
            apply()
        }
    }

    fun getToken():String?{
        return pref.getString(Constants.FCM_TOKEN, "")
    }

    fun deletePrefs(){
        pref.edit().apply {
            clear()
            apply()
        }
    }

    fun setUser(user: User){
        pref.edit().apply {
            putString(Constants.USER_FIRSTNAME, user.firstName)
            putString(Constants.USER_LASTNAME, user.lastName)
            putString(Constants.USER_SCHOOLID, user.schoolId)
            putString(Constants.USER_EMAIL, user.email)
            putString(Constants.USER_PHONENUMBER, user.phoneNumber)
            putString(Constants.USER_IMAGEURL, user.imageUri)
            putBoolean(Constants.HAS_USER_PAY, user.hasUserPay)
            putString(Constants.USER_PAYMENT_REF, user.userPaymentRef)
            putString(Constants.FCM_TOKEN, user.fcmToken)
            putString(Constants.USER_CREATED_BY_WHO, user.userCreatedByWho)
            putLong(Constants.USER_CREATED_WHEN, user.userCreatedWhen?:0)
            putInt(Constants.USER_USERWHO, user.userWho)
            putString(Constants.USER_UNIQUEID, user.uniqueId)
            putString(Constants.PASSWORD, user.password)
            putString(Constants.PROGRAMME, user.programme)
            putString(Constants.USER_COLLEGE, user.college)
            putString(Constants.USER_DEPT, user.dept)
            putString(Constants.USER_ENTRYYEAR, user.entryYear)
            putString(Constants.USER_HALL, user.hallOfResidence)
            putString(Constants.USER_HALL_NUMBER, user.hallRoomNumber)
            apply()
        }
    }

    fun getUser():User{
        return User().apply {
            firstName = pref.getString(Constants.USER_FIRSTNAME, "")!!
            lastName = pref.getString(Constants.USER_LASTNAME, "")!!
            schoolId = pref.getString(Constants.USER_SCHOOLID, "")!!
            email = pref.getString(Constants.USER_EMAIL, "")!!
            phoneNumber = pref.getString(Constants.USER_PHONENUMBER, "")!!
            imageUri = pref.getString(Constants.USER_IMAGEURL, "")!!
            hasUserPay = pref.getBoolean(Constants.HAS_USER_PAY, false)
            userPaymentRef = pref.getString(Constants.USER_PAYMENT_REF, "")
            fcmToken = pref.getString(Constants.FCM_TOKEN, "")
            userCreatedByWho = pref.getString(Constants.USER_CREATED_BY_WHO, "")
            userCreatedWhen = pref.getLong(Constants.USER_CREATED_WHEN, 0)
            userWho = pref.getInt(Constants.USER_USERWHO, -1)
            uniqueId = pref.getString(Constants.USER_UNIQUEID, "")
            password = pref.getString(Constants.PASSWORD, "")?:""
            college = pref.getString(Constants.USER_COLLEGE, "")
            dept = pref.getString(Constants.USER_DEPT, "")
            entryYear = pref.getString(Constants.USER_ENTRYYEAR, "")
            hallOfResidence = pref.getString(Constants.USER_HALL, "")
            hallRoomNumber = pref.getString(Constants.USER_HALL_NUMBER, "")
        }
    }
}