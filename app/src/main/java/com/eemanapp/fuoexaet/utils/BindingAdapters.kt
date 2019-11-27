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


@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}

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

@BindingAdapter("goneIfNotSecurity")
fun goneLayoutIfNotSecurity(view: View, it: Int){
    view.visibility = if (it == 2) View.VISIBLE else View.GONE
}

@BindingAdapter("goneIfNotStaff")
fun goneLayoutIfNotStaff(view: View, it: Int){
    view.visibility = if (it == 1) View.VISIBLE else View.GONE
}

@BindingAdapter("requestStatusColor")
fun TextView.setRequestStatusColor(requestStatus: String) {
    when (requestStatus) {
        DiffExaetStatus.APPROVED.name -> {
            this.setTextColor(ContextCompat.getColor(this.context, R.color.approved_request_lightGreen))
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
fun TextView.setRequestColor(requestType:String){
    when (requestType){
        "Emergency Exeat" -> this.setTextColor(ContextCompat.getColor(this.context, R.color.rejected_request_red))
        "Vacation Exeat" -> this.setTextColor(ContextCompat.getColor(this.context, R.color.total_request_purple))
        else -> this.setTextColor(ContextCompat.getColor(this.context, R.color.black))
    }
}