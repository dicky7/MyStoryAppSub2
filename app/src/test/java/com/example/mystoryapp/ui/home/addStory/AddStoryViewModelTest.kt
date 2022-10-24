package com.example.mystoryapp.ui.home.addStory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystoryapp.data.MyStoryRepository
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.remote.response.UploadStoryResponse
import com.example.mystoryapp.utils.DataDummy
import com.example.mystoryapp.utils.MainCoroutineRule
import com.example.mystoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
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
class AddStoryViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var myStoryRepository: MyStoryRepository
    private lateinit var addStoryViewModel: AddStoryViewModel

    private val dummyAddStoryResponse = DataDummy.generateAddStoryResponse()
    private val dummyToken = "my_token"
    private val dummyDescription = "description".toRequestBody("text/plain".toMediaType())
    private val dummyImage = MultipartBody.Part.createFormData("photo", "dummyFile")
    private val dummyLat = 0.0f
    private val dummyLang = 0.0f

    @Before
    fun setup(){
        addStoryViewModel = AddStoryViewModel(myStoryRepository)
    }

    @get:Rule
    val coroutineRule= MainCoroutineRule()

    @Test
    fun `When Get Auth Token Success`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = dummyToken
        `when`(myStoryRepository.getAuthToken()).thenReturn(expectedToken)

        val actualToken = addStoryViewModel.getAuthToken().getOrAwaitValue()
        Mockito.verify(myStoryRepository).getAuthToken()
        Assert.assertNotNull(actualToken)
        Assert.assertEquals(dummyToken, actualToken)
    }

    @Test
    fun `When Cant Get Auth Token Success and token is null`() = runTest {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = null
        `when`(myStoryRepository.getAuthToken()).thenReturn(expectedToken)

        val actualToken = addStoryViewModel.getAuthToken().getOrAwaitValue()
        Mockito.verify(myStoryRepository).getAuthToken()
        Assert.assertNull(actualToken)
    }

    @Test
    fun `When Success To Add Story`() = coroutineRule.runTest {
        val expectedResponse = MutableLiveData<Result<UploadStoryResponse>>()
        expectedResponse.value = Result.Success(dummyAddStoryResponse)
        `when`(myStoryRepository.uploadStories(dummyToken,dummyImage,dummyDescription,dummyLat,dummyLang)).thenReturn(expectedResponse)

        val actualResponse = addStoryViewModel.uploadStories(dummyToken,dummyImage,dummyDescription,dummyLat,dummyLang).getOrAwaitValue()
        Mockito.verify(myStoryRepository).uploadStories(dummyToken,dummyImage,dummyDescription,dummyLat,dummyLang)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
        Assert.assertEquals(dummyAddStoryResponse.message, (actualResponse as Result.Success).data.message)
    }

    @Test
    fun `When Failed to Add Story`() = coroutineRule.runTest {
        val expectedResponse = MutableLiveData<Result<UploadStoryResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(myStoryRepository.uploadStories(dummyToken,dummyImage,dummyDescription,dummyLat,dummyLang)).thenReturn(expectedResponse)

        val actualResponse = addStoryViewModel.uploadStories(dummyToken,dummyImage,dummyDescription,dummyLat,dummyLang).getOrAwaitValue()
        Mockito.verify(myStoryRepository).uploadStories(dummyToken,dummyImage,dummyDescription,dummyLat,dummyLang)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}