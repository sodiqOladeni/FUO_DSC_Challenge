package com.eemanapp.fuoexaet.di

import com.eemanapp.fuoexaet.view.main.home.FilterDialog
import com.eemanapp.fuoexaet.view.main.home.HomeDashboardFragment
import com.eemanapp.fuoexaet.view.main.profile.ProfileFragment
import com.eemanapp.fuoexaet.view.main.home.NewRequestFragment
import com.eemanapp.fuoexaet.view.main.home.RequestProfileDetailsFragment
import com.eemanapp.fuoexaet.view.main.profile.PasswordResetFragment
import com.eemanapp.fuoexaet.view.main.profile.SignupStaffFragment
import com.eemanapp.fuoexaet.view.main.requests.PlaceholderFragment
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

    @ContributesAndroidInjector
    abstract fun bindNewRequestFragment(): NewRequestFragment

    @ContributesAndroidInjector
    abstract fun bindPlaceholderFragment(): PlaceholderFragment

    @ContributesAndroidInjector
    abstract fun bindRequestProfileDetailsFragment(): RequestProfileDetailsFragment

    @ContributesAndroidInjector
    abstract fun bindSignupStaffFragment():SignupStaffFragment

    @ContributesAndroidInjector
    abstract fun bindPasswordResetFragment():PasswordResetFragment

    @ContributesAndroidInjector
    abstract fun bindFilterDialog():FilterDialog
}