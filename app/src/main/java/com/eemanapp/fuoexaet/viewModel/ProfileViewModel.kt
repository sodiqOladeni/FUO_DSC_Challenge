package com.eemanapp.fuoexaet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.model.UiData
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Methods
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private var pref: SharedPref) : ViewModel() {

    private var fireStoreDoc: DocumentReference? = null
    private var fireStore = FirebaseFirestore.getInstance()
    private val newUiData = UiData()
    private val _uiData = MutableLiveData<UiData>()
    val uiData: LiveData<UiData>
        get() = _uiData
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
        getUser()
    }

    private fun getUser() {
        _user.value = getUserFromPref()
    }

    private fun getUserFromPref(): User {
        return pref.getUser()
    }

    fun updateUserData(user: User) {
        fireStoreDoc =
            fireStore.collection(Methods.userWhoCodeToName(user.userWho)).document(user.uniqueId!!)
        fireStoreDoc?.set(user)?.addOnCompleteListener {
            if (it.isSuccessful) {
                // Handle Success
                newUiData.status = true
                newUiData.message = "Profile successfully updated"
                _uiData.value = newUiData
                updateUserInPref(user)
            } else {
                // Handle failures
                newUiData.status = false
                newUiData.message = it.exception?.localizedMessage ?: "Error while updating user information"
                _uiData.value = newUiData
            }
        }
    }

    private fun updateUserInPref(user: User) {
        pref.setUser(user)
    }
}
