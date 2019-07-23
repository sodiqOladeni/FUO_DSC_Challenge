package com.eemanapp.fuoexaet.di

import com.eemanapp.fuoexaet.view.main.HomeDashboardFragment
import com.eemanapp.fuoexaet.view.main.ProfileFragment
import com.eemanapp.fuoexaet.view.main.RequestsFragment
import com.eemanapp.fuoexaet.view.main.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainFragmentBindingModule {

    @ContributesAndroidInjector
    abstract fun bindHomeDashboardFragment(): HomeDashboardFragment

    @ContributesAndroidInjector
    abstract fun bindProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun bindRequestsFragment(): RequestsFragment

    @ContributesAndroidInjector
    abstract fun bindSettingsFragment(): SettingsFragment
}