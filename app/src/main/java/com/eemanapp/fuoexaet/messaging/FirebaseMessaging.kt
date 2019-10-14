package com.eemanapp.fuoexaet.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.eemanapp.fuoexaet.R
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.Methods
import com.eemanapp.fuoexaet.view.main.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessaging : FirebaseMessagingService() {

    val TAG = "FirebaseMessaging"

    override fun onMessageReceived(notificationData: RemoteMessage) {
        try {
            if (isUserActive()) {
                super.onMessageReceived(notificationData)
            } else {
                composeAndSendNotification(notificationData)
            }
        } catch (e: Exception) {

        }
    }

    private fun composeAndSendNotification(messageData: RemoteMessage) {
        Log.v(TAG, "Message Active ==>" + messageData.data)
        val data = messageData.data
        // Create an explicit intent for an Activity in your app
        val mainIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.main_navigation_graph)
            .setDestination(R.id.homeDashboardFragment)
            .setArguments(null)
            .createPendingIntent()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        //Create BroadcastReceiver for the Approve
        val approveIntent = Intent(this, ApproveDeclineBroadcastReceiver::class.java).apply {
            action = Constants.ACTION_APPROVE
            putExtra(Constants.REQUEST_ID, data["requestId"])
        }
        val approvePendingIntent = PendingIntent.getBroadcast(this, 0, approveIntent, 0)

        //Create BroadcastReceiver for the Approve
        val declineIntent = Intent(this, ApproveDeclineBroadcastReceiver::class.java).apply {
            action = Constants.ACTION_DECLINE
            putExtra(Constants.REQUEST_ID, data["requestId"])
        }
        val declinePendingIntent = PendingIntent.getBroadcast(this, 0, declineIntent, 0)

        //Create Notification channel for Oreo Device and above
        createNotificationChannel()

        val ft = Glide.with(this).asBitmap().load(data["icon"]).submit()

        val builder = NotificationCompat.Builder(
            this,
            Constants.REQUEST_NOTIFICATION_CHANNEL_ID
        ).apply {
            setContentTitle(data["title"])
            setContentText(data["location"])
            setSmallIcon(R.drawable.ic_small_notification)
            setLargeIcon(ft.get())
            setStyle(NotificationCompat.BigTextStyle().bigText(data["body"]))
            setAutoCancel(true)
            setContentIntent(mainIntent)
//            addAction(R.drawable.ic_approve_notification, Constants.APPROVE, approvePendingIntent)
//            addAction(R.drawable.ic_decline_notification, Constants.DECLINE, declinePendingIntent)
            priority = NotificationCompat.PRIORITY_HIGH
        }

        Glide.with(this).clear(ft)
        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Constants.STUDENT_REQUEST_CHANNEL
            val descriptionText =
                Constants.STUDENT_REQUEST_CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                Constants.REQUEST_NOTIFICATION_CHANNEL_ID,
                name, importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        val context = requireNotNull(applicationContext)
        updateToken(token, context)
    }

    private fun updateToken(token: String, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val pref = SharedPref(context)
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