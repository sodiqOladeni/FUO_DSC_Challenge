package com.eemanapp.fuoexaet.viewModel

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.model.UiData
import com.eemanapp.fuoexaet.model.User
import com.eemanapp.fuoexaet.utils.Methods
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.UploadTask
import javax.inject.Inject
import android.content.Intent.getIntent
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class SignupViewModel @Inject constructor(var pref: SharedPref) :
    ViewModel() {

    private val _uiData = MutableLiveData<UiData>()
    val uiData: LiveData<UiData>
        get() = _uiData
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorageRef = FirebaseStorage.getInstance().reference
    private val fireStore = FirebaseFirestore.getInstance()
    private val newUiData = UiData()
    private var createUserTask: Task<AuthResult>? = null
    private var uploadTask: UploadTask? = null
    private var fireStoreDoc: DocumentReference? = null

    fun authUserAndProceedSaving(user: User) {
        createUserTask = firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
        createUserTask?.addOnCompleteListener {
            if (it.isSuccessful) {
                user.uniqueId = firebaseAuth.currentUser?.uid
                saveUserImage(user)
            } else {
                newUiData.status = false
                newUiData.message = it.exception?.message ?: "Error while creating user"
                _uiData.value = newUiData
            }
        }
    }

    private fun saveUserImage(user: User) {
        val ref = firebaseStorageRef.child("FUO_Exaet_Main")
            .child(Methods.userWhoCodeToName(user.userWho))
            .child("Profile_Images")
            .child(user.uniqueId!!)
            .child(UUID.randomUUID().toString() + ".jpg")

        uploadTask = ref.putFile(user.imageUri.toUri())
        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            // Continue with the task to get the download URL
            ref.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.imageUri = task.result.toString()
                saveUserData(user)
            } else {
                // Handle failures
                newUiData.status = false
                newUiData.message =
                    task.exception?.localizedMessage ?: "Error while uploading image"
                _uiData.value = newUiData
            }
        }
    }

    private fun saveUserData(user: User) {
        fireStoreDoc =
            fireStore.collection(Methods.userWhoCodeToName(user.userWho)).document(user.uniqueId!!)
        fireStoreDoc?.set(user)?.addOnCompleteListener {
            if (it.isSuccessful) {
                // Handle Success
                sendVerificationEmail()
            } else {
                // Handle failures
                newUiData.status = false
                newUiData.message = it.exception?.localizedMessage ?: "Error while saving data"
                _uiData.value = newUiData
            }
        }
    }

    fun saveUiDataToDefault() {
        _uiData.value = null
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                FirebaseAuth.getInstance().signOut()
                if (task.isSuccessful) {
                    // email sent
                    // after email is sent just logout the user and finish this activity
                    newUiData.status = true
                    newUiData.message = "User successfully created, please check your email to verify your account"
                    _uiData.value = newUiData
                } else {
                    newUiData.status = false
                    newUiData.message = "User successfully created but unable to send verification email"
                    _uiData.value = newUiData
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        createUserTask = null
        uploadTask = null
        fireStoreDoc = null
    }
}
