package com.eemanapp.fuoexaet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.data.local.UserDao
import com.eemanapp.fuoexaet.model.UiData
import com.eemanapp.fuoexaet.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LoginViewModel @Inject constructor(var userDao: UserDao, var pref:SharedPref) : ViewModel() {
    private val _uiData = MutableLiveData<UiData>()
    val uiData: LiveData<UiData>
        get() = _uiData
    private var mAuth = FirebaseAuth.getInstance()
    private var mFirestore = FirebaseFirestore.getInstance()
    private var authTask: Task<AuthResult>? = null
    private var newUiData = UiData()

    fun authUser(email: String, password: String, userWho: String) {
        authTask = mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                newUiData.status = it.isSuccessful
                newUiData.message = ""
                fetchUserDataWithEmail(email, userWho)
            } else {
                newUiData.status = false
                newUiData.message = it.exception?.message ?: "Unable to login user"
                _uiData.value = newUiData
            }
        }
    }

    private fun fetchUserDataWithEmail(e: String, userWho: String) {
        val doc = mFirestore.collection(userWho)
        val query = doc.whereEqualTo("email", e).limit(1)
        query.get().addOnSuccessListener {
            if (!it.documents.isNullOrEmpty()) {
                //User to be saved in the db after creating it
                // And also userId to be in shared preferences
                val user = it.toObjects(User::class.java)
                userDao.setUser(user[0])
                newUiData.status = true
                newUiData.message = "User found"
                _uiData.value = newUiData
            }
        }.addOnFailureListener {
            newUiData.status = false
            newUiData.message = it.message ?: "User does not exist, signup instead"
            _uiData.value = newUiData
        }
    }

    fun setUiDataToNull() {
        _uiData.value = null
    }

    override fun onCleared() {
        super.onCleared()
        authTask = null
    }
}
