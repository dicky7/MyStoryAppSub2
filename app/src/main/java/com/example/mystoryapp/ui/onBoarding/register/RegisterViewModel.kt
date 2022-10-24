package com.example.mystoryapp.ui.onBoarding.register

import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.MyStoryRepository

class RegisterViewModel(private val myStoryRepository: MyStoryRepository) : ViewModel() {
    // TODO: Implement the ViewModel
    fun register(name:String, email:String, password:String) = myStoryRepository.register(name, email, password)
}