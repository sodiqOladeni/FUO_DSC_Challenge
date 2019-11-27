package com.eemanapp.fuoexaet.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.eemanapp.fuoexaet.model.Request
import com.google.android.material.button.MaterialButton
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class Methods {

    companion object {
        fun showProgressBar(progressView: View, btnToHide: MaterialButton, buttons: List<View>) {
            progressView.visibility = View.VISIBLE
            btnToHide.visibility = View.GONE

            for (button in buttons) {
                button.isEnabled = false
                button.isClickable = false
                button.alpha = 0.5F
            }
        }

        fun hideProgressBar(progressView: View, btnToHide: MaterialButton, buttons: List<View>) {
            progressView.visibility = View.GONE
            btnToHide.visibility = View.VISIBLE

            for (button in buttons) {
                button.isEnabled = true
                button.isClickable = true
                button.alpha = 1F
            }
        }

        fun hideSoftKey(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun isValidPassword(password: String): Boolean {
            return if (TextUtils.isEmpty(password)) {
                false
            } else {
                password.length > 5
            }
        }

        fun isValidEmail(email: String): Boolean {
            return if (TextUtils.isEmpty(email)) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }

        fun showSuccessDialog(
            context: Context,
            title: String,
            message: String,
            confirmationMessage: String = "Ok"
        ): SweetAlertDialog {
            val dialogBuilder = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            dialogBuilder.apply {
                titleText = title
                contentText = message
                confirmText = confirmationMessage
                setConfirmClickListener {
                    this.dismiss()
                }
                show()
            }
            return dialogBuilder
        }

        fun showNotSuccessDialog(
            context: Context,
            title: String,
            message: String,
            confirmationMessage: String = "Ok"
        ): SweetAlertDialog {
            val dialogBuilder = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            dialogBuilder.apply {
                titleText = title
                contentText = message
                confirmText = confirmationMessage
                show()
            }
            return dialogBuilder
        }

        fun showNormalDialog(context: Context, title: String, message: String, confirmationMessage: String = "Ok", cancelMessage: String = "Cancel"
        ): SweetAlertDialog {
            val dialogBuilder = SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
            dialogBuilder.apply {
                titleText = title
                contentText = message
                confirmText = confirmationMessage
                cancelText = cancelMessage
                show()
            }
            return dialogBuilder
        }

        // This is the user type
        // 0 ==> Student
        // 1 ==> Student coordinator
        // 2 ==> Security
        fun userWhoNameToCode(name: String): Int {
            return when (name) {
                Constants.STUDENT -> 0
                Constants.COORDINATOR -> 1
                Constants.SECURITY -> 2
                else -> 0
            }
        }

        // This is the user type
        // Student ==> 0
        // Student coordinator ==> 1
        // Security ==> 2
        fun userWhoCodeToName(code: Int): String {
            return when (code) {
                0 -> Constants.STUDENT
                1 -> Constants.COORDINATOR
                2 -> Constants.SECURITY
                else -> Constants.STUDENT
            }
        }

        /**
         * Take the Long milliseconds returned by the system and stored in Room,
         * and convert it to a nicely formatted string for display.
         *
         * EEEE - Display the long letter version of the weekday
         * MMM - Display the letter abbreviation of the nmotny
         * dd-yyyy - day in month and full year numerically
         * HH:mm - Hours and minutes in 24hr format
         */
        @SuppressLint("SimpleDateFormat")
        fun convertLongToDateString(systemTime: Long): String {
            return SimpleDateFormat("E, dd MMM yyyy' :'HH:mm")
                .format(systemTime).toString()
        }

        @SuppressLint("SimpleDateFormat")
        fun formatDate(systemTime: Long): String {
            return SimpleDateFormat("E, dd MMM yyyy").format(systemTime).toString()
        }

        @SuppressLint("SimpleDateFormat")
        fun formatDateRemove1900(year: Int, month: Int, dayOfMonth: Int): String {
            val date = Date(year - 1900, month, dayOfMonth)
            return SimpleDateFormat("E, dd MMM yyyy").format(date).toString()
        }

        @SuppressLint("SimpleDateFormat")
        fun formatTime(systemTime: Long): String {
            return SimpleDateFormat("HH:mm a").format(systemTime).toString()
        }

        @SuppressLint("SimpleDateFormat")
        fun formatTime(hours: Int, minutes: Int): String {
            val time = Time(hours, minutes, 0)
            return SimpleDateFormat("HH:mm a").format(time).toString()
        }

        fun getAllRequestApprovedCount(requests: List<Request>): List<Request> {
            return requests.filter { it.requestStatus == DiffExaetStatus.APPROVED.name }
        }

        fun getAllRequestDeclinedCount(requests: List<Request>): List<Request> {
            return requests.filter { it.requestStatus == DiffExaetStatus.DECLINED.name }
        }

        fun getAllRequestPendingCount(requests: List<Request>): List<Request> {
            return requests.filter { it.requestStatus == DiffExaetStatus.PENDING.name }
        }

        fun countRequestsToday(
            requests: List<Request>?,
            today: Int,
            thisMonth: Int,
            thisYear: Int
        ): Int {
            return requests?.filter {
                (Date(it.requestTime).day == today) and (Date(it.requestTime).month == thisMonth) and (Date(
                    it.requestTime
                ).year == thisYear)
            }?.size ?: 0
        }

        fun countRequestsApprovedToday(
            requests: List<Request>?,
            today: Int,
            thisMonth: Int,
            thisYear: Int
        ): Int {
            return requests?.filter {
                (it.requestStatus == DiffExaetStatus.APPROVED.name) and
                        ((Date(it.requestTime).day == today) and (Date(it.requestTime).month == thisMonth) and (Date(
                            it.requestTime
                        ).year == thisYear))
            }?.size ?: 0
        }

        fun countRequestsDeclinedToday(
            requests: List<Request>?,
            today: Int,
            thisMonth: Int,
            thisYear: Int
        ): Int {
            return requests?.filter {
                (it.requestStatus == DiffExaetStatus.DECLINED.name) and
                        ((Date(it.requestTime).day == today) and (Date(it.requestTime).month == thisMonth) and (Date(
                            it.requestTime
                        ).year == thisYear))
            }?.size ?: 0
        }

        fun countRequestPendingToday(
            requests: List<Request>?,
            today: Int,
            thisMonth: Int,
            thisYear: Int
        ): Int {
            return requests?.filter {
                (it.requestStatus == DiffExaetStatus.PENDING.name) and
                        ((Date(it.requestTime).day == today) and (Date(it.requestTime).month == thisMonth) and (Date(
                            it.requestTime
                        ).year == thisYear))
            }?.size ?: 0
        }

        fun countUserRegularRequestThisMonth(requests: List<Request>?): Int {
            return requests?.filter {
                (Date(it.requestTime).month == Date(System.currentTimeMillis()).month)
                        && Date(it.requestTime).year == Date(System.currentTimeMillis()).year
                        && it.requestType == "Regular Exeat"
            }?.size ?: 0
        }


        @Suppress("DEPRECATION")
        fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm?.run {
                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                        result = when {
                            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                            else -> false
                        }
                    }
                }
            } else {
                cm?.run {
                    cm.activeNetworkInfo?.run {
                        if (type == ConnectivityManager.TYPE_WIFI) {
                            result = true
                        } else if (type == ConnectivityManager.TYPE_MOBILE) {
                            result = true
                        }
                    }
                }
            }
            return result
        }

        @Suppress("DEPRECATION")
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }


        fun isWifiEnabled(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return wifiNetwork != null && wifiNetwork.isConnected
        }
    }
}