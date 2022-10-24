package com.example.mystoryapp.data

import com.example.mystoryapp.data.remote.response.GetStoriesResponse
import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.data.remote.response.RegisterResponse
import com.example.mystoryapp.data.remote.response.UploadStoryResponse
import com.example.mystoryapp.data.remote.retrofit.ApiService
import com.example.mystoryapp.utils.DataDummy
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService:ApiService {
    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return DataDummy.generateRegisterResponse()
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        return DataDummy.generateLoginResponse()
    }

    override suspend fun getStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int
    ): GetStoriesResponse {
        TODO("Not yet implemented")
    }

    override suspend fun uploadStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        lon: Float?
    ): UploadStoryResponse {
        TODO("Not yet implemented")
    }
}