package com.example.mystoryapp.ui.onBoarding.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.MyStoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val myStoryRepository: MyStoryRepository) : ViewModel() {
    fun login(email:String, password:String) = myStoryRepository.login(email,password)

    /**
     * using viewModeScope for running coruntine in ViewModel and  becasuse viewModelScope lifecyle aware
     */
    fun saveAuthToken(token:String){
        viewModelScope.launch {
            myStoryRepository.saveAuthToken(token)
        }
    }
}