package com.example.mystoryapp.ui.home.homeStory

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.mystoryapp.JsonConverter
import com.example.mystoryapp.R
import com.example.mystoryapp.data.remote.retrofit.ApiConfig
import com.example.mystoryapp.utlis.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class HomeFragmentTest{

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getStoriesList_Success(){
        launchFragmentInContainer<HomeFragment>(themeResId = R.style.Theme_MyStoryApp)
        val stringBody = JsonConverter.readStringFromFile("stories_success_response.json")
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(stringBody)

        mockWebServer.enqueue(mockResponse)
        
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
        onView(withText("Kevin")).check(matches(isDisplayed()))
    }

    @Test
    fun getStoriesList_Failed(){
        launchFragmentInContainer<HomeFragment>(themeResId = R.style.Theme_MyStoryApp)
        val stringBody = JsonConverter.readStringFromFile("stories_error_response.json")
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(stringBody)

        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.text_view_error)).check(matches(isDisplayed()))
        onView(withId(R.id.imageView_error)).check(matches(isDisplayed()))
    }
}