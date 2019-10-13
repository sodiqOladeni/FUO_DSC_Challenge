package com.eemanapp.fuoexaet.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.eemanapp.fuoexaet.utils.DiffExaetStatus
import kotlinx.android.parcel.Parcelize
import javax.annotation.Nonnull

data class FeatureRequestsAndErrorReport(
    var featureOrError: String = "",
    var user: User? = null
)