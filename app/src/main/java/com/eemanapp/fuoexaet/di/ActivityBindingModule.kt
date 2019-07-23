package com.eemanapp.fuoexaet.di

import com.eemanapp.fuoexaet.view.main.MainActivity
import com.eemanapp.fuoexaet.view.prep.PrepActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityBindingModule {
    @ContributesAndroidInjector(modules = [PrepFragmentBindingModule::class])
    abstract fun mainFtue(): PrepActivity

    @ContributesAndroidInjector(modules = [MainFragmentBindingModule::class])
    abstract fun mainActivity(): MainActivity
}