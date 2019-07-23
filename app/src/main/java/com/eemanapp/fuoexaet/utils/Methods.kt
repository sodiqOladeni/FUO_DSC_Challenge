package com.eemanapp.fuoexaet.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import cn.pedant.SweetAlert.SweetAlertDialog

class Methods {

    companion object {
        fun showProgressBar(progressView: View, buttons: List<View>) {
            progressView.visibility = View.VISIBLE
            for (button in buttons) {
                button.isEnabled = false
                button.isClickable = false
            }
        }

        fun hideProgressBar(progressView: View, buttons:List<View>) {
            progressView.visibility = View.GONE
            for (button in buttons) {
                button.isEnabled = true
                button.isClickable = true
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
                password.length > 7
            }
        }

        fun isValidEmail(email: String): Boolean {
            return if (TextUtils.isEmpty(email)) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }

        fun showSuccessDialog(context: Context, title: String, message: String, confirmationMessage:String = "Ok"): SweetAlertDialog {
            val dialogBuilder = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            dialogBuilder.apply {
                titleText = title
                contentText = message
                confirmText = confirmationMessage
                show()
            }
            return dialogBuilder
        }

        fun showNotSuccessDialog(context: Context, title: String, message: String, confirmationMessage:String = "Ok"): SweetAlertDialog {
            val dialogBuilder = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            dialogBuilder.apply {
                titleText = title
                contentText = message
                confirmText = confirmationMessage
                show()
            }
            return dialogBuilder
        }
    }
}