package com.eemanapp.fuoexaet.view

import android.app.Activity
import android.app.Application
import com.eemanapp.fuoexaet.di.AppInjector
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class MainApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        AppInjector.init(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector
}