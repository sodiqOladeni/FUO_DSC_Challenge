package com.eemanapp.fuoexaet.di

import android.app.Application
import com.eemanapp.fuoexaet.view.MainApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@SuppressWarnings("unchecked")
@Singleton
@Component(modules = [DataModule::class,
     AndroidSupportInjectionModule::class, ActivityBindingModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(application: MainApplication)
}
