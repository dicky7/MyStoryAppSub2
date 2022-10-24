package com.example.mystoryapp.ui.home.location

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystoryapp.data.MyStoryRepository
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.remote.response.GetStoriesResponse
import com.example.mystoryapp.ui.home.addStory.AddStoryViewModel
import com.example.mystoryapp.utils.DataDummy
import com.example.mystoryapp.utils.MainCoroutineRule
import com.example.mystoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var myStoryRepository: MyStoryRepository
    private lateinit var mapViewModel: MapViewModel

    private val dummyStoryResponse = DataDummy.generateDummyListStoryLocation()
    private val dummyToken = "my_token"
    private val dummyPage = null
    private val dummySize = 30

    @Before
    fun setup(){
        mapViewModel = MapViewModel(myStoryRepository)
    }

    @Test
    fun `When Get Auth Token Success`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = dummyToken
        Mockito.`when`(myStoryRepository.getAuthToken()).thenReturn(expectedToken)

        val actualToken = mapViewModel.getAuthToken().getOrAwaitValue()
        Mockito.verify(myStoryRepository).getAuthToken()
        assertNotNull(actualToken)
        assertEquals(dummyToken, actualToken)
    }

    @Test
    fun `When Cant Get Auth Token Success and token is null`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = null
        Mockito.`when`(myStoryRepository.getAuthToken()).thenReturn(expectedToken)

        val actualToken = mapViewModel.getAuthToken().getOrAwaitValue()
        Mockito.verify(myStoryRepository).getAuthToken()
        assertNull(actualToken)
    }

    @Test
    fun `When Success Get List Story`() = coroutineRule.runTest {
        val expectedResponse = MutableLiveData<Result<GetStoriesResponse>>()
        expectedResponse.value = Result.Success(dummyStoryResponse)
        `when`(myStoryRepository.getStoriesLocation(dummyToken, dummyPage, dummySize)).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getStoriesLocation(dummyToken,dummyPage,dummySize).getOrAwaitValue()
        Mockito.verify(myStoryRepository).getStoriesLocation(dummyToken, dummyPage,dummySize)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertEquals(dummyStoryResponse.message, (actualResponse as Result.Success).data.message)
    }

    @Test
    fun `When Failed Get List Story`() = coroutineRule.runTest {
        val expectedResponse = MutableLiveData<Result<GetStoriesResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(myStoryRepository.getStoriesLocation(dummyToken, dummyPage, dummySize)).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getStoriesLocation(dummyToken, dummyPage,dummySize).getOrAwaitValue()
        Mockito.verify(myStoryRepository).getStoriesLocation(dummyToken, dummyPage,dummySize)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}