package com.eemanapp.fuoexaet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.model.UiData
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Methods
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import javax.inject.Inject


class SignupCreateStaffViewModel @Inject constructor(var pref: SharedPref) :
    ViewModel() {

    private val newUiData = UiData()
    private var fireStoreDoc: DocumentReference? = null
    private var fireStore = FirebaseFirestore.getInstance()

     fun saveUserData(user: User) : MutableLiveData<UiData>{
         val uiData = MutableLiveData<UiData>()
        fireStoreDoc = fireStore.collection(Methods.userWhoCodeToName(user.userWho)).document()
         user.uniqueId = fireStoreDoc?.id
        fireStoreDoc?.set(user)?.addOnCompleteListener {
            if (it.isSuccessful) {
                // Handle Success
                newUiData.status = true
                newUiData.message = "Account was successfully created, the user can proceed to login using the credentials provided"
                uiData.value = newUiData
            } else {
                // Handle failures
                newUiData.status = false
                newUiData.message = it.exception?.localizedMessage ?: "Error while creating account"
                uiData.value = newUiData
            }
        }
         return uiData
    }

    override fun onCleared() {
        super.onCleared()
        fireStoreDoc = null
    }
}
