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
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangePasswordViewModel::class)
    abstract fun bindChangePasswordViewModel(changePasswordViewModel: ChangePasswordViewModel):ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignupCreateStaffViewModel::class)
    abstract fun bindSignupCreateStaffViewModel(signupCreateStaffViewModel: SignupCreateStaffViewModel):ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory):ViewModelProvider.Factory
}