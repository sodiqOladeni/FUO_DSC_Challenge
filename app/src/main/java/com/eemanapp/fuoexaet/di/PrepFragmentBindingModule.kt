package com.eemanapp.fuoexaet.di

import com.eemanapp.fuoexaet.view.prep.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
 abstract class PrepFragmentBindingModule {

    @ContributesAndroidInjector
    abstract fun bindSplashScreen():SplashActivity

    @ContributesAndroidInjector
    abstract fun bindCarouselSliderFragment(): CarouselSliderFragment

    @ContributesAndroidInjector
    abstract fun bindSignup(): SignupFragment

    @ContributesAndroidInjector
    abstract fun bindlogin(): LoginFragment

   @ContributesAndroidInjector
   abstract fun bindForget(): ForgetPasswordFragment
}