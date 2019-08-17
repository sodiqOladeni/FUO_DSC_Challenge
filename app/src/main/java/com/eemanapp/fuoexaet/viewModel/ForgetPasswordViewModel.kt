package com.eemanapp.fuoexaet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.model.UiData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ForgetPasswordViewModel : ViewModel() {

    private val _uiData = MutableLiveData<UiData>()
    val uiData: LiveData<UiData>
        get() = _uiData
    private var mAuth = FirebaseAuth.getInstance()
    private var authTask: Task<Void>? = null
    private var newUiData = UiData()

    fun resetPasswordWithEmail(email: String) {
        authTask = mAuth.sendPasswordResetEmail(email).addOnSuccessListener {
            newUiData.status = true
            newUiData.message = "Password reset link has been sent to your email"
            _uiData.value = newUiData
        }.addOnFailureListener {
            newUiData.status = false
            newUiData.message = it.message
            _uiData.value = newUiData
        }
    }

    fun uiDataToNull() {
        _uiData.value = null
    }

    override fun onCleared() {
        super.onCleared()
        authTask = null
    }
}
