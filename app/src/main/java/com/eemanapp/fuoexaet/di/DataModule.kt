package com.eemanapp.fuoexaet.di

import android.app.Application
import android.content.Context
import com.eemanapp.fuoexaet.data.SharedPref
import com.eemanapp.fuoexaet.data.local.FuoDb
import com.eemanapp.fuoexaet.data.local.UserDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class DataModule {

    @Singleton
    @Provides
    fun providePref(app:Application): SharedPref = SharedPref(app)

    @Singleton
    @Provides
    fun provideDatabase(app:Application):FuoDb = FuoDb.getDatabase(app)

    @Singleton
    @Provides
    fun providesFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()


    @Singleton
    @Provides
    fun provideuserDao(appDb: FuoDb):UserDao = appDb.userDao
}