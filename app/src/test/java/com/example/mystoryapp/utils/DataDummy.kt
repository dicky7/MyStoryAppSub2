package com.example.mystoryapp.utils

import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.remote.response.*

object DataDummy {
    fun generateDummyListStoryLocation(): GetStoriesResponse {
        val listStory = mutableListOf<ListStoryItem>()
        for (i in 0 until 10) {
            val story = ListStoryItem(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )
            listStory.add(story)
        }
        return GetStoriesResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = listStory
        )
    }

    fun generateDummyListStory(): List<StoryEntity> {
        val items = arrayListOf<StoryEntity>()
        for (i in 0 until 10) {
            val story = StoryEntity(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )

            items.add(story)
        }

        return items
    }

    fun generateLoginResponse(): LoginResponse{
        val loginResult = LoginResult(
            userId = "user-yj5pc_LARC_AgK61",
            name =  "Arif Faizin",
            token = "my_token"
        )
        return LoginResponse(
            loginResult,
            error = false,
            message = "success"
        )
    }

    fun generateRegisterResponse(): RegisterResponse =
        RegisterResponse(
            error = false,
            message = "User Created"
    )

    fun generateAddStoryResponse():  UploadStoryResponse=
        UploadStoryResponse(
            error = false,
            message = "success"
        )

}