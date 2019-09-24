package com.eemanapp.fuoexaet.utils

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.eemanapp.fuoexaet.model.Request

@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}

/**
 * Binding adapter used to display images from URL using Glide
 */
@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    url.let {
        Glide.with(imageView.context).load(url).into(imageView)
    }
}

@BindingAdapter("goneIfNull")
fun setLayoutVisibility(view: View, it: String?) {
    view.visibility = if (it == null) View.GONE else View.VISIBLE
}

//@BindingAdapter("requestStatus")
//fun setRequestStatus(
//    txtView: TextView,
//    requestStatus: String,
//    whoTemplate: String,
//    whoConfirm: String
//) {
//    when (requestStatus) {
//        DiffExaetStatus.APPROVED.name -> {
//            txtView.text = String.format(whoTemplate, "Approved by:", whoConfirm)
//            txtView.setTextColor(Color.parseColor("#10C971"))
//
//        }
//
//        DiffExaetStatus.DECLINED.name -> {
//            txtView.text = String.format(whoTemplate, "Declined by:", whoConfirm)
//            txtView.setTextColor(Color.parseColor("#EB5757"))
//
//        }
//
//        DiffExaetStatus.OUT_SCHOOL.name -> {
//            txtView.text = "Student Out of School"
//            txtView.setTextColor(Color.parseColor("#F2C94C"))
//
//        }
//
//        DiffExaetStatus.COMPLETED.name -> {
//            txtView.text = "Request Completed"
//            txtView.setTextColor(Color.parseColor("#463C6A"))
//        }
//    }
//}

@BindingAdapter("convertSystemTimeToReadableTime")
fun convertSystemTimeToReadableTIme(view: TextView, systemTime: Long) {
    view.text = Methods.convertLongToDateString(systemTime)
}