package com.example.mystoryapp.data

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.example.mystoryapp.data.local.DataStoreManager
import com.example.mystoryapp.data.local.database.StoryDatabase
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.remote.response.*
import com.example.mystoryapp.data.remote.retrofit.ApiService
import com.example.mystoryapp.utlis.generateBearerToken
import okhttp3.MultipartBody
import okhttp3.RequestBody


class MyStoryRepository (
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
    private val storyDatabase: StoryDatabase
    ){

    companion object{
        private var instance: MyStoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            dataStoreManager: DataStoreManager,
            storyDatabase: StoryDatabase
        ):MyStoryRepository = instance ?: synchronized(this){
            instance ?: MyStoryRepository(apiService, dataStoreManager, storyDatabase)
        }.also { instance = it }
    }


    /**
     *Handle login process for user by calling login Api from apiService
     *
     * @param name
     * @param email
     * @param password
     * @return liveData
     */
    fun register(name:String, email:String, password:String):LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.register(name,email,password)
            emit(Result.Success(registerResponse))
        }catch (e:Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    /**
     *Handle register process for user by calling register Api apiService
     *
     * @param email
     * @param password
     * @return liveData
     */
    fun login(email:String, password:String):LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val loginResponse = apiService.login(email,password)
            emit(Result.Success(loginResponse))
        }catch (e:Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    /**
     * Get stories List data from paging data source
     *
     * @param token user loin
     *
     * @return  liveData
     */
    fun getStories(token: String): LiveData<PagingData<StoryEntity>>{
        val bearerToken = generateBearerToken(token)
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, bearerToken),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    /**
     * Get stories List by calling API from apiServices
     *
     * @param token user loin
     * @param page optional
     * @param size optional
     *
     * @return  liveData
     */
    fun getStoriesLocation(
        token: String,
        page: Int? = null,
        size: Int? = null
    ): LiveData<Result<GetStoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val bearerToken = generateBearerToken(token)
            val stories = apiService.getStories(bearerToken, page, size, 1)
            emit(Result.Success(stories))
        }catch (e:Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    /**
     * Upload Story by calling Related API
     *
     * @param token user loin
     * @param file selected image
     * @param description story description
     *
     * @return  liveData
     */
    fun uploadStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ): LiveData<Result<UploadStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val tokenLogin = generateBearerToken(token)
            val uploadStoryResponse = apiService.uploadStories(tokenLogin, file, description, lat, lon)
            emit(Result.Success(uploadStoryResponse))
        }catch (e:Exception){
            emit(Result.Error(e.message.toString()))
        }
    }
    /**
     * Save user's authentication token to the preferences
     *
     * @param token User's authentication token
     * @return Unit
     */
    suspend fun saveAuthToken(token:String){
        dataStoreManager.saveAuthToken(token)
    }

    /**
     * Get the user's authentication token from preferences
     *
     * @return flow
     */
    fun getAuthToken(): LiveData<String> {
        return dataStoreManager.getAuthToken().asLiveData()
    }

    /**
     * Get the user's logged in state
     *
     * @return flow
     */
    fun isLoggedIn(): LiveData<Boolean>{
        return  dataStoreManager.isLoggedIn().asLiveData()
    }

    /**
     * set state login. when state is true go to homeStories and when state false go to splash screen
     *
     */
    suspend fun setLoggedIn(isLogin:Boolean){
        return dataStoreManager.setLoggedIn(isLogin)
    }


}