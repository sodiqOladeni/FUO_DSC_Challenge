package com.eemanapp.fuoexaet.utils

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.api.load
import coil.transform.CircleCropTransformation
import com.eemanapp.fuoexaet.R
import java.util.regex.Pattern

/**
 * Binding adapter used to display images from URL using Glide
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    imageView.load(url) {
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}

@BindingAdapter("goneIfNull")
fun setLayoutVisibility(view: View, it: String?) {
    view.visibility = if (it == null) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIfNotSecurity")
fun goneLayoutIfNotSecurity(view: View, it: Int) {
    view.visibility = if (it == 2) View.VISIBLE else View.GONE
}

@BindingAdapter("goneIfNotStaff")
fun goneLayoutIfNotStaff(view: View, it: Int) {
    view.visibility = if (it == 1) View.VISIBLE else View.GONE
}

@BindingAdapter("goneIfNotHOD")
fun goneLayoutIfNotHOD(view: View, it: Int) {
    view.visibility = if (it == 3) View.VISIBLE else View.GONE
}

fun hideOrShowHodLayout(requestType: String, hodName: String?): Boolean {
    return (requestType == "Vacation Exeat") && (hodName == null)
}

@BindingAdapter("requestStatusColor")
fun TextView.setRequestStatusColor(requestStatus: String) {
    when (requestStatus) {
        DiffExaetStatus.APPROVED.name -> {
            this.setTextColor(
                ContextCompat.getColor(
                    this.context,
                    R.color.approved_request_lightGreen
                )
            )
        }

        DiffExaetStatus.DECLINED.name -> {
            this.setTextColor(ContextCompat.getColor(this.context, R.color.rejected_request_red))
        }

        DiffExaetStatus.OUT_SCHOOL.name -> {
            this.setTextColor(ContextCompat.getColor(this.context, R.color.pending_request_yellow))
        }

        DiffExaetStatus.COMPLETED.name -> {
            this.setTextColor(ContextCompat.getColor(this.context, R.color.total_request_purple))
        }
    }
}

@BindingAdapter("convertSystemTimeToReadableTime")
fun convertSystemTimeToReadableTIme(view: TextView, systemTime: Long) {
    view.text = Methods.convertLongToDateString(systemTime)
}

@BindingAdapter("setRequestColor")
fun TextView.setRequestColor(requestType: String) {
    when (requestType) {
        "Emergency Exeat" -> this.setTextColor(
            ContextCompat.getColor(
                this.context,
                R.color.rejected_request_red
            )
        )
        "Vacation Exeat" -> this.setTextColor(
            ContextCompat.getColor(
                this.context,
                R.color.total_request_purple
            )
        )
        else -> this.setTextColor(ContextCompat.getColor(this.context, R.color.black))
    }
}

@BindingAdapter("hideViewOrVisible")
fun hideViewOrVisible(view: View, requestType: String?) {
    requestType?.let {
        view.visibility = if (it == "Vacation Exeat") View.VISIBLE else View.GONE
    }
}

@BindingAdapter("hideViewOrVisible")
fun hideViewOrVisible(view: View, isVacationExeat: Boolean) {
    view.visibility = if (isVacationExeat) View.VISIBLE else View.GONE
}

// ==> Example of Valid Matric Number FUO/19/0297
fun String.isValidMatriculationNumber(): Boolean {
    return this.matches("^FUO/[0-9]{2}/[0-9]{4}$".toRegex(setOf(RegexOption.IGNORE_CASE)))
}