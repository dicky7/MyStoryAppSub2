package com.example.mystoryapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.example.mystoryapp.data.local.DataStoreManager
import com.example.mystoryapp.data.local.database.StoryDatabase
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.remote.response.GetStoriesResponse
import com.example.mystoryapp.data.remote.response.UploadStoryResponse
import com.example.mystoryapp.data.remote.retrofit.ApiService
import com.example.mystoryapp.ui.home.homeStory.ListStoryAdapter
import com.example.mystoryapp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
class MyStoryRepositoryTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var  dataStoreManager: DataStoreManager
    @Mock
    private lateinit var storyDatabase: StoryDatabase
    @Mock
    private lateinit var myStoryRepositoryMock: MyStoryRepository

    private lateinit var myStoryRepository: MyStoryRepository
    private lateinit var apiService: ApiService



    private val dummyEmail = "myEmail@gmail.com"
    private val dummyPassword = "myPassword"
    private val dummyUsername = "myUsername"
    private val dummyToken = "myToken"
    private val dummyPage = null
    private val dummySize = 30

    @Before
    fun setup(){
        apiService = FakeApiService()
        myStoryRepository = MyStoryRepository(apiService,dataStoreManager,storyDatabase)
    }

    @Test
    fun `When Login User Return Message Success`() = coroutineRule.runBlockingTest{
        val expectedLoginResponse = DataDummy.generateLoginResponse()
        val actualLoginResponse = apiService.login(dummyEmail,dummyPassword)
        Assert.assertNotNull(actualLoginResponse)
        Assert.assertEquals(expectedLoginResponse.message, actualLoginResponse.message)

    }

    @Test
    fun `When Register User Return Message Success`() = coroutineRule.runBlockingTest {
        val expectedRegisterResponse = DataDummy.generateRegisterResponse()
        val actualRegisterResponse = apiService.register(dummyUsername,dummyEmail,dummyPassword)
        Assert.assertNotNull(actualRegisterResponse)
        Assert.assertEquals(expectedRegisterResponse.message, actualRegisterResponse.message)
    }

    @Test
    fun `when Get Stories Should Not Null and Return Success`() = coroutineRule.runTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val data = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data
        Mockito.`when`(myStoryRepositoryMock.getStories(dummyToken)).thenReturn(expectedStory)

        val actualStory = myStoryRepositoryMock.getStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories, differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0].name, differ.snapshot()[0]?.name)

    }

    @Test
    fun `when Get Stories Location Should Not Null and Return Success`() = coroutineRule.runTest {
        val expectedResponse = MutableLiveData<Result<GetStoriesResponse>>()
        expectedResponse.value = Result.Success(DataDummy.generateDummyListStoryLocation())
        `when`(myStoryRepositoryMock.getStoriesLocation(dummyToken, dummyPage, dummySize)).thenReturn(expectedResponse)

        val actualStory = myStoryRepositoryMock.getStoriesLocation(dummyToken, dummyPage, dummySize).getOrAwaitValue()
        Assert.assertTrue(actualStory is Result.Success)
        Assert.assertTrue(actualStory is Result.Success)
        Assert.assertEquals(DataDummy.generateDummyListStoryLocation().message, (actualStory as Result.Success).data.message)
    }

}