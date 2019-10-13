package com.eemanapp.fuoexaet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.model.UiData
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class ChangePasswordViewModel @Inject constructor() : ViewModel() {

    private var mAuth = FirebaseAuth.getInstance()
    private var newUiData = UiData()

    fun resetPasswordWithEmail(email: String,
        oldPassword: String, newPassword: String): MutableLiveData<UiData> {
        val uiData = MutableLiveData<UiData>()
        val user = mAuth.currentUser
        val credential = EmailAuthProvider.getCredential(email, oldPassword)
        user?.reauthenticate(credential)?.addOnCompleteListener { reAuth ->
            if (reAuth.isSuccessful) {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        newUiData.status = true
                        newUiData.message = "Password has been successfully changed"
                        uiData.value = newUiData
                    }

                    .addOnFailureListener { e ->
                        newUiData.status = false
                        newUiData.message = e.message
                        uiData.value = newUiData
                    }
            } else {
                newUiData.status = false
                newUiData.message = reAuth.exception?.message ?: "Failed to confirm initial credentials"
                uiData.value = newUiData
            }
        }
        return uiData
    }
}
