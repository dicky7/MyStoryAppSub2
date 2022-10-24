package com.example.mystoryapp.ui.onBoarding.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystoryapp.data.MyStoryRepository
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.data.remote.response.RegisterResponse
import com.example.mystoryapp.ui.onBoarding.login.LoginViewModel
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
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var myStoryRepository: MyStoryRepository
    private lateinit var registerViewModel: RegisterViewModel

    private val dummyRegisterResponse = DataDummy.generateRegisterResponse()
    private val dummyEmail = "myEmail@gmail.com"
    private val dummyPassword = "myPassword"
    private val dummyUsername = "myUsername"

    @Before
    fun setup(){
        registerViewModel = RegisterViewModel(myStoryRepository)
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `when register success should return Success`() = mainCoroutineRule.runTest {
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.Success(dummyRegisterResponse)

        Mockito.`when`(myStoryRepository.register(dummyUsername,dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualRegister = registerViewModel.register(dummyUsername,dummyEmail,dummyPassword).getOrAwaitValue()
        Mockito.verify(myStoryRepository).register(dummyUsername,dummyEmail,dummyPassword)
        Assert.assertNotNull(actualRegister)
        Assert.assertTrue(actualRegister is Result.Success)
        Assert.assertEquals(dummyRegisterResponse.message, (actualRegister as Result.Success).data.message)

    }

    @Test
    fun `when login failed should return Error`() = mainCoroutineRule.runTest {
        val expectedResponse = MutableLiveData<Result<RegisterResponse>>()
        expectedResponse.value = Result.Error("Error")

        Mockito.`when`(myStoryRepository.register(dummyUsername,dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualRegister = registerViewModel.register(dummyUsername,dummyEmail,dummyPassword).getOrAwaitValue()
        Mockito.verify(myStoryRepository).register(dummyUsername,dummyEmail,dummyPassword)
        Assert.assertNotNull(actualRegister)
        Assert.assertTrue(actualRegister is Result.Error)
    }


}