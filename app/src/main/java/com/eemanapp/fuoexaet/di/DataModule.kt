package com.eemanapp.fuoexaet.di

import dagger.Module

@Module(includes = [ViewModelModule::class])
class DataModule {

//    @Singleton
//    @Provides
//    fun providePref(app: Application): SharedPreferenceManager = SharedPreferenceManager(app)

//    @Singleton
//    @Provides
//    fun provideDatabase(app: Application):SqyteDatabase = SqyteDatabase.getDatabase(app)


//    @Singleton
//    @Provides
//    fun provideLoginSignupRepo(api:RestApi, preferenceManager: SharedPreferenceManager):
//            LoginSignupRepo = LoginSignupRepo(api, preferenceManager)
//
//    @Singleton
//    @Provides
//    fun provideCourseRepository(api: RestApi):CoursesRepository = CoursesRepository(api)
//
//    @Singleton
//    @Provides
//    fun provideProfileRepository(api: RestApi, preferenceManager: SharedPreferenceManager):
//            ProfileRepository = ProfileRepository(api, preferenceManager)

//    @Singleton
//    @Provides
//    fun getRetrofit(): RestApi {
//        val httpClient = OkHttpClient.Builder()
//        httpClient.connectTimeout(240, TimeUnit.SECONDS)
//        val apiInterceptor = HttpLoggingInterceptor()
//        apiInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        httpClient.readTimeout(120, TimeUnit.SECONDS)
//        httpClient.connectTimeout(240, TimeUnit.SECONDS)
//        if (BuildConfig.DEBUG) {
//            httpClient.addInterceptor(apiInterceptor)
//        }
//
//        return Retrofit.Builder()
//            .baseUrl(Constants.BASE_URL)
//            .client(httpClient.build())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build().create(RestApi::class.java)
//    }
}