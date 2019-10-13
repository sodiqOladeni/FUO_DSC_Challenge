package com.eemanapp.fuoexaet.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.data.local.RequestDao
import com.eemanapp.fuoexaet.model.FeatureRequestsAndErrorReport
import com.eemanapp.fuoexaet.model.UiData
import com.eemanapp.fuoexaet.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import javax.inject.Inject

class SettingsViewModel @Inject constructor(var pref: SharedPref, var requestDao: RequestDao) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var _isUserSignOut = MutableLiveData<Boolean>()
    val isUserSignOut: LiveData<Boolean>
        get() = _isUserSignOut

    fun deleteUserFromDb() {
        uiScope.launch {
            withContext(Dispatchers.IO){
                requestDao.deleteRequests()
            }
            pref.deletePrefs()
            FirebaseAuth.getInstance().signOut()
            _isUserSignOut.value = true
        }
    }

    private val newUiData = UiData()
    private var fireStoreDoc: DocumentReference? = null
    private var fireStore = FirebaseFirestore.getInstance()

    fun saveFeatureRequest(featureRequestsAndErrorReport: FeatureRequestsAndErrorReport):MutableLiveData<UiData> {
        val uiData = MutableLiveData<UiData>()
        fireStoreDoc = fireStore.collection("Feature_Requests_And_Error_Report").document()
        fireStoreDoc?.set(featureRequestsAndErrorReport)?.addOnCompleteListener {
            if (it.isSuccessful) {
                // Handle Success
                newUiData.status = true
                newUiData.message = "Your request was successfully recorded, Enjoy your day :)"
                uiData.value = newUiData
            } else {
                // Handle failures
                newUiData.status = false
                newUiData.message = it.exception?.localizedMessage ?: "Error submitting request"
                uiData.value = newUiData
            }
        }
        return uiData
    }

    fun doneSignOut(){
        _isUserSignOut.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        fireStoreDoc = null
    }
}