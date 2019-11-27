package com.eemanapp.fuoexaet.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.model.UiData
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LoginViewModel @Inject constructor(var pref: SharedPref) : ViewModel() {
    private var mAuth = FirebaseAuth.getInstance()
    private var mFirestore = FirebaseFirestore.getInstance()
    private var authTask: Task<AuthResult>? = null

    fun authUser(email: String, password: String, userWho: String):MutableLiveData<UiData> {
        val uiData = MutableLiveData<UiData>()
        val newUiData = UiData()
        authTask = mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                if (it.user?.isEmailVerified!!) {
                    val query = mFirestore.collection(userWho).whereEqualTo("email", email).limit(1)
                    query.get().addOnSuccessListener {qShots->
                        if (!qShots.documents.isNullOrEmpty()) {
                            //User to be saved in the db after creating it
                            // And also userId to be in shared preferences
                            val user = qShots.toObjects(User::class.java)
                            if (user[0].hasUserPay) {
                                newUiData.status = true
                                newUiData.message = "User found"
                                saveUserInfo(user[0])
                                uiData.value = newUiData
                            } else {
                                newUiData.status = false
                                newUiData.message = Constants.USER_NOT_PAY
                                newUiData.data = user[0]
                                uiData.value = newUiData
                            }
                        } else {
                            newUiData.status = false
                            newUiData.message =
                                "User does not exist please confirm you are on the right page or signup instead"
                            uiData.value = newUiData
                        }
                    }.addOnFailureListener {e->
                        newUiData.status = false
                        newUiData.message = e.message ?: "Error fetching user data"
                        uiData.value = newUiData
                    }
                } else {
                    it.user?.sendEmailVerification()
                    FirebaseAuth.getInstance().signOut()
                    newUiData.status = false
                    newUiData.message =
                        "Your email has not been verified, please check your email and verify your account"
                    uiData.value = newUiData
                }
            }
            .addOnFailureListener {
                newUiData.status = false
                newUiData.message = it.message ?: "Unable to login user"
                uiData.value = newUiData
            }
        return uiData
    }

    fun updateUserInfo(user: User): MutableLiveData<UiData> {
        val uiData = MutableLiveData<UiData>()
        val newUiData = UiData()
        val userTable =
            mFirestore.collection(Methods.userWhoCodeToName(user.userWho)).document(user.uniqueId!!)
        userTable.update("hasUserPay", user.hasUserPay, "userPaymentRef", user.userPaymentRef)
            .addOnSuccessListener {
                newUiData.status = true
                newUiData.message = "Payment successfully recorded, well done!"
                saveUserInfo(user)
                uiData.value = newUiData
            }
            .addOnFailureListener {
                newUiData.status = false
                newUiData.message = "Payment has been made but not yet recorded, " +
                        "please report this issue via fuoexaet@gmail.com"
                uiData.value = newUiData
            }
        return uiData
    }

    private fun saveUserInfo(user: User) {
        pref.setUser(user)
        pref.setUserId(user.uniqueId!!)
    }

    override fun onCleared() {
        super.onCleared()
        authTask = null
    }
}
