package com.eemanapp.fuoexaet.interfaces

import android.widget.CompoundButton
import com.eemanapp.fuoexaet.model.Request

interface RequestClickListener {
    fun onViewProfile(request: Request)
    fun onRequestDecline(request: Request)
    fun onRequestApprove(request: Request)
    fun onVacationApproved(request: Request, checkBoxView:CompoundButton, isChecked:Boolean)
}