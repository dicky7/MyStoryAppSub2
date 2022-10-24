package com.example.mystoryapp.ui.home.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.MyStoryRepository
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.remote.response.GetStoriesResponse
import com.example.mystoryapp.data.remote.response.ListStoryItem

class MapViewModel(private val myStoryRepository: MyStoryRepository): ViewModel() {
    fun getAuthToken(): LiveData<String> = myStoryRepository.getAuthToken()
    fun getStoriesLocation(token: String, page: Int?, size:Int): LiveData<Result<GetStoriesResponse>> = myStoryRepository.getStoriesLocation(token,page, size)
}