package com.eemanapp.fuoexaet.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eemanapp.fuoexaet.viewModel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignupViewModel::class)
    abstract fun bindSignupViewModel(signupViewModel: SignupViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgetPasswordViewModel::class)
    abstract fun bindForgetPasswordViewModel(forgetPasswordViewModel: ForgetPasswordViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeDashboardViewModel::class)
    abstract fun bindHomeDashboardViewModel(homeDashboardViewModel: HomeDashboardViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RequestsViewModel::class)
    abstract fun bindRequestsViewModel(requestsViewModel: RequestsViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel):ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory):ViewModelProvider.Factory
}