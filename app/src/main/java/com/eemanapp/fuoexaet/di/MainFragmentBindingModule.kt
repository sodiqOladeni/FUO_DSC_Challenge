package com.eemanapp.fuoexaet.di

import com.eemanapp.fuoexaet.view.main.home.HomeDashboardFragment
import com.eemanapp.fuoexaet.view.main.ProfileFragment
import com.eemanapp.fuoexaet.view.main.requests.RequestsFragment
import com.eemanapp.fuoexaet.view.main.settings.SettingsFragment
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