package com.example.mystoryapp.data.remote.retrofit

import com.example.mystoryapp.data.remote.response.GetStoriesResponse
import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.data.remote.response.RegisterResponse
import com.example.mystoryapp.data.remote.response.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    /**
     * Api for handle register process
     *
     * @param name
     * @param email
     * @param password
     * @return RegisterResponse
     */
    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name:String,
        @Field("email") email:String,
        @Field("password") password: String
    ):RegisterResponse

    /**
     * Api for handle login process
     *
     * @param email
     * @param password
     * @return LoginResponse
     */
    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email:String,
        @Field("password") password: String
    ):LoginResponse

    /**
     * Api for getAllStories
     *
     * @param token user login
     * @param page optional
     * @param size optional
     * @return LoginResponse
     */
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int
    ): GetStoriesResponse


    /**
     * Api for UploadStories
     *
     * @param tokenuser login
     * @param file select image as File
     * @param description story description
     * @return UploadStoryResponse
     */
    @Multipart
    @POST("stories")
    suspend fun uploadStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?
    ):UploadStoryResponse
}