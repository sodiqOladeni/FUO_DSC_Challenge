package com.eemanapp.fuoexaet.messaging

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.utils.Constants
import com.eemanapp.fuoexaet.utils.DiffExaetStatus
import com.google.firebase.firestore.FirebaseFirestore

class ApproveDeclineBroadcastReceiver :BroadcastReceiver() {
    val TAG = "BroadcastReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val user = SharedPref(context).getUser()
            val db = FirebaseFirestore.getInstance()

            if (intent?.action == Constants.ACTION_APPROVE) {
                val requestId = intent.extras?.getString(Constants.REQUEST_ID)
                requestId?.let {
                    Log.v(TAG, "RequestId"+it)
                    db.collection(Constants.ALL_REQUESTS).document(it).update(
                        "requestStatus", DiffExaetStatus.APPROVED.name,
                        "declineOrApproveTime", System.currentTimeMillis(),
                        "approveCoordinator", "${user.firstName} ${user.lastName}"
                    )
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Log.v(TAG, "Approve Succeed")
                            } else {
                                Log.v(TAG, "Approve Failed")
                            }
                        }
                }
            } else if (intent?.action == Constants.ACTION_DECLINE) {
                val requestId = intent.extras?.getString(Constants.REQUEST_ID)
                requestId?.let {
                    Log.v(TAG, "RequestId"+it)
                    db.collection(Constants.ALL_REQUESTS).document(it).update(
                        "requestStatus", DiffExaetStatus.DECLINED.name,
                        "declineOrApproveTime", System.currentTimeMillis(),
                        "approveCoordinator", "${user.firstName} ${user.lastName}"
                    )
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Log.v(TAG, "Decline Succeed")
                            } else {
                                Log.v(TAG, "Decline Failed")
                            }
                        }
                }
            }
        }
    }
}