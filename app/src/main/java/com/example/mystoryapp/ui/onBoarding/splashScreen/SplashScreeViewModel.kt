package com.example.mystoryapp.ui.onBoarding.splashScreen

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.MyStoryRepository
import kotlinx.coroutines.flow.Flow

class SplashScreeViewModel(private val myStoryRepository: MyStoryRepository): ViewModel() {
    fun isLoggedIn(): LiveData<Boolean> = myStoryRepository.isLoggedIn()
}