package com.eemanapp.fuoexaet.viewModel

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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


class SignupViewModel : ViewModel() {

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
                user.uniqueId = firebaseAuth.currentUser?.providerId
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
            fireStore.collection(Methods.userWhoCodeToName(user.userWho)).document(user.fullName)
        fireStoreDoc?.set(user)?.addOnCompleteListener {
            if (it.isSuccessful) {
                // Handle failures
                newUiData.status = it.isSuccessful
                newUiData.message = it.result.toString()
                _uiData.value = newUiData

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

    override fun onCleared() {
        super.onCleared()
        createUserTask = null
        uploadTask = null
        fireStoreDoc = null
    }
}
