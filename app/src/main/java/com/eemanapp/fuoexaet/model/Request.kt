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

@Parcelize
@Entity(tableName = "request")
data class Request(
    @PrimaryKey
    @Nonnull var requestUniqueId: String = "",
    var requestType: String = "",
    var requestStatus: String? = "",
    var hasHODApproved:Boolean? = false,
    var approveHOD:String? = null,
    var hodApproveTime:Long? = null,
    var approveCoordinator: String? = null,
    var declineOrApproveTime:Long? = null,
    var departureDate: String = "",
    var departureTime: String = "",
    var arrivalDate: String = "",
    var arrivalTime: String = "",
    var location: String = "",
    var purpose: String = "",
    var requestTime: Long = 0L,
    var gateDepartureTime: Long? = null,
    var gateArrivalTime: Long? = null,
    @Embedded var user: User? = null
) : Parcelable {

    val getStatusWithCoordinator: String
        get() {
            return when (requestStatus) {
                DiffExaetStatus.APPROVED.name -> {
                    "${requestStatus?.toLowerCase()?.capitalize()} by: $approveCoordinator"
                }

                DiffExaetStatus.DECLINED.name -> {
                    "${requestStatus?.toLowerCase()?.capitalize()} by: $approveCoordinator"
                }

                DiffExaetStatus.OUT_SCHOOL.name -> {
                    "Student Out of School"
                }

                DiffExaetStatus.COMPLETED.name -> {
                    "Request Completed"
                }
                else -> {
                    ""
                }
            }
        }
}