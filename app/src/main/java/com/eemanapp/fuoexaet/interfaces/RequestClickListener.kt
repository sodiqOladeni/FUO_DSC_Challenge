package com.eemanapp.fuoexaet.interfaces

import com.eemanapp.fuoexaet.model.Request

interface RequestClickListener {
    fun onViewProfile(request: Request)
    fun onRequestDecline(request: Request)
    fun onRequestApprove(request: Request)
}