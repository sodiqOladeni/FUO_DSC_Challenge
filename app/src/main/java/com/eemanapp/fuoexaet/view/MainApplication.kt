package com.eemanapp.fuoexaet.view

import android.app.Activity
import android.app.Application
import android.app.Service
import co.paystack.android.PaystackSdk
import com.eemanapp.fuoexaet.di.AppInjector
import com.google.android.play.core.missingsplits.MissingSplitsManagerFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import javax.inject.Inject

class MainApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if (MissingSplitsManagerFactory.create(this).disableAppIfMissingRequiredSplits()) {
            // Skip app initialization.
            return
        }
        AppInjector.init(this)
        PaystackSdk.initialize(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector
}