package com.example.mystoryapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.mystoryapp.data.MyStoryRepository
import com.example.mystoryapp.data.local.DataStoreManager
import com.example.mystoryapp.data.local.database.StoryDatabase
import com.example.mystoryapp.data.remote.retrofit.ApiConfig


object Injection {
    fun provideRepository(context: Context): MyStoryRepository{
        val apiService = ApiConfig.getApiService()
        val dataStoreManager = DataStoreManager(context)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return MyStoryRepository.getInstance(apiService, dataStoreManager, storyDatabase)
    }
}