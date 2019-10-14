package com.eemanapp.fuoexaet.utils

import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.view.main.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import javax.inject.Inject

class FirebaseMessaging : FirebaseMessagingService() {

    @Inject
    lateinit var pref: SharedPref
    @Inject
    lateinit var db: FirebaseFirestore

    override fun onMessageReceived(notificationData: RemoteMessage) {
        AndroidInjection.inject(this)
        if (isUserActive()) {
            super.onMessageReceived(notificationData)
        } else {
            composeAndSendNotification(notificationData)
        }
    }

    private fun composeAndSendNotification(messageData: RemoteMessage){

    }

    override fun onNewToken(token: String) {
        updateToken(token)
    }

    private fun updateToken(token: String) {
        val user = pref.getUser()
        db.collection(Methods.userWhoCodeToName(user.userWho)).document(user.uniqueId!!)
            .update("fcmToken", token).addOnCompleteListener {
                if (it.isSuccessful) {
                    pref.setToken(token)
                }
            }
    }

    private fun isUserActive(): Boolean {
        return MainActivity.isActive
    }
}