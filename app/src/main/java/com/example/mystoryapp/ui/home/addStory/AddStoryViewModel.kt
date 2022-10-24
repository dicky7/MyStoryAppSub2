package com.example.mystoryapp.ui.home.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.MyStoryRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart

class AddStoryViewModel(private val myStoryRepository: MyStoryRepository) : ViewModel() {
    // TODO: Implement the ViewModel
    fun getAuthToken(): LiveData<String> = myStoryRepository.getAuthToken()

    fun uploadStories(
        token:String,
        file:MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ) = myStoryRepository.uploadStories(token, file, description, lat, lon)
}