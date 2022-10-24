package com.example.mystoryapp.utlis

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.MyStoryRepository
import com.example.mystoryapp.di.Injection
import com.example.mystoryapp.ui.home.addStory.AddStoryViewModel
import com.example.mystoryapp.ui.home.homeStory.HomeViewModel
import com.example.mystoryapp.ui.home.location.MapViewModel
import com.example.mystoryapp.ui.onBoarding.login.LoginViewModel
import com.example.mystoryapp.ui.onBoarding.register.RegisterViewModel
import com.example.mystoryapp.ui.onBoarding.splashScreen.SplashScreeViewModel

class ViewModelFactory private constructor(private val myStoryRepository: MyStoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(myStoryRepository) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(myStoryRepository) as T
        }
        if (modelClass.isAssignableFrom(SplashScreeViewModel::class.java)) {
            return SplashScreeViewModel(myStoryRepository) as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(myStoryRepository) as T
        }
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(myStoryRepository) as T
        }
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(myStoryRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}