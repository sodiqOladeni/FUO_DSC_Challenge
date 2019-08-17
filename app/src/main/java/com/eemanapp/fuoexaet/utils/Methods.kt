package com.eemanapp.fuoexaet.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.button.MaterialButton
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.ArrayList

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
            return SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm")
                .format(systemTime).toString()
        }

    }
}