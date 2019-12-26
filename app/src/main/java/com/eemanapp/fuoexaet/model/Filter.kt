package com.eemanapp.fuoexaet.model

import com.eemanapp.fuoexaet.utils.DiffExaetStatus
import java.util.*
import kotlin.collections.ArrayList

data class Filter(var startDate: Long? = null,
                  var endingDate:Long? = null,
                  var exeatsType:ArrayList<String>? = null,
                  var requestsStatus:ArrayList<String>? = null) {

    fun getAllExeatType():ArrayList<String>{
        return arrayListOf("Regular Exeat", "Vacation Exeat", "Emergency Exeat")
    }

    fun getAllRequestStatus():ArrayList<String>{
        return arrayListOf(DiffExaetStatus.APPROVED.name, DiffExaetStatus.DECLINED.name, DiffExaetStatus.PENDING.name,
            DiffExaetStatus.OUT_SCHOOL.name, DiffExaetStatus.COMPLETED.name)
    }
}