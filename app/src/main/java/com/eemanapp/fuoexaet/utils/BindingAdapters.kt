package com.eemanapp.fuoexaet.utils

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.api.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide

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
fun setRequestStatusColor(txtView: TextView, requestStatus: String) {
    when (requestStatus) {
        DiffExaetStatus.APPROVED.name -> {
            txtView.setTextColor(Color.parseColor("#10C971"))
        }

        DiffExaetStatus.DECLINED.name -> {
            txtView.setTextColor(Color.parseColor("#EB5757"))
        }

        DiffExaetStatus.OUT_SCHOOL.name -> {
            txtView.setTextColor(Color.parseColor("#F2C94C"))
        }

        DiffExaetStatus.COMPLETED.name -> {
            txtView.setTextColor(Color.parseColor("#463C6A"))
        }
    }
}

@BindingAdapter("convertSystemTimeToReadableTime")
fun convertSystemTimeToReadableTIme(view: TextView, systemTime: Long) {
    view.text = Methods.convertLongToDateString(systemTime)
}