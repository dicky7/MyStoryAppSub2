package com.example.mystoryapp.ui.onBoarding.splashScreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.mystoryapp.data.MyStoryRepository
import com.example.mystoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.flow.flowOf
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

@RunWith(MockitoJUnitRunner::class)
class SplashScreeViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var myStoryRepository: MyStoryRepository
    private lateinit var splashScreeViewModel: SplashScreeViewModel
    private val dummyIsLoggedIn = true

    @Before
    fun setup(){
        splashScreeViewModel = SplashScreeViewModel(myStoryRepository)
    }

    @Test
    fun `When Get IsLoggedIn Success`(){
        val expectedIsLoggedIn = MutableLiveData<Boolean>()
        expectedIsLoggedIn.value = dummyIsLoggedIn
        `when`(myStoryRepository.isLoggedIn()).thenReturn(expectedIsLoggedIn)

        val actualIsLoggedIn = splashScreeViewModel.isLoggedIn().getOrAwaitValue()
        Mockito.verify(myStoryRepository).isLoggedIn()
        Assert.assertNotNull(actualIsLoggedIn)
        Assert.assertEquals(dummyIsLoggedIn, actualIsLoggedIn)
    }

    @Test
    fun `When Get IsLoggedIn Failed`(){
        val expectedIsLoggedIn = MutableLiveData<Boolean>()
        expectedIsLoggedIn.value = false
        `when`(myStoryRepository.isLoggedIn()).thenReturn(expectedIsLoggedIn)

        val actualIsLoggedIn = splashScreeViewModel.isLoggedIn().getOrAwaitValue()
        Mockito.verify(myStoryRepository).isLoggedIn()
        Assert.assertNotNull(actualIsLoggedIn)
        Assert.assertNotEquals(dummyIsLoggedIn, actualIsLoggedIn)
    }

}