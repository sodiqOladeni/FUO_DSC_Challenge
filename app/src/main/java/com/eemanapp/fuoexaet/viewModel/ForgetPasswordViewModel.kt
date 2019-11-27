package com.eemanapp.fuoexaet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.model.UiData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ForgetPasswordViewModel @Inject constructor() : ViewModel() {

    private var mAuth = FirebaseAuth.getInstance()
    private var authTask: Task<Void>? = null

    fun resetPasswordWithEmail(email: String):MutableLiveData<UiData> {
        val uiData = MutableLiveData<UiData>()
        val newUiData = UiData()
        authTask = mAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                newUiData.status = true
                newUiData.message = "Password reset link has been sent to your email"
                uiData.value = newUiData
            }
            .addOnFailureListener {
                newUiData.status = false
                newUiData.message = it.message
                uiData.value = newUiData
            }
        return uiData
    }


    override fun onCleared() {
        super.onCleared()
        authTask = null
    }
}
