package com.example.mystoryapp.ui.home.homeStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystoryapp.data.MyStoryRepository
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class HomeViewModel(private val myStoryRepository: MyStoryRepository):ViewModel() {
    fun getAuthToken(): LiveData<String> = myStoryRepository.getAuthToken()

    fun getStories(token: String): LiveData<PagingData<StoryEntity>> = myStoryRepository.getStories(token).cachedIn(viewModelScope)

    /**
     * set state login
     *
     * using viewModeScope for running coruntine in ViewModel and  becasuse viewModelScope lifecyle aware
     */
    fun setIsLoggedIn(isBoolean: Boolean) {
        viewModelScope.launch {
            myStoryRepository.setLoggedIn(isBoolean)
        }
    }
}