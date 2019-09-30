package com.eemanapp.fuoexaet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.data.local.RequestDao
import com.google.firebase.auth.FirebaseAuth
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

    fun doneSignOut(){
        _isUserSignOut.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}