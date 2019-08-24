package com.eemanapp.fuoexaet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.local.UserDao
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SettingsViewModel @Inject constructor(var userDao: UserDao) : ViewModel() {

    private var _isUserSignOut = MutableLiveData<Boolean>()
    val isUserSignOut: LiveData<Boolean>
        get() = _isUserSignOut

    fun deleteUserFromDb() {
        userDao.deleteUser()
        FirebaseAuth.getInstance().signOut()
        _isUserSignOut.value = true
    }

    fun doneSignOut(){
        _isUserSignOut.value = false
    }
}