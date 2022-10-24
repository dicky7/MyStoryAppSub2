package com.example.mystoryapp.ui.onBoarding.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.mystoryapp.data.MyStoryRepository
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.utils.DataDummy
import com.example.mystoryapp.utils.MainCoroutineRule
import com.example.mystoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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
class LoginViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var myStoryRepository: MyStoryRepository
    private lateinit var loginViewModel: LoginViewModel

    private val dummyLoginResponse = DataDummy.generateLoginResponse()
    private val dummyEmail = "myEmail@gmail.com"
    private val dummyPassword = "myPassword"
    private val dummyToken = "my_token"

    @Before
    fun setup(){
        loginViewModel = LoginViewModel(myStoryRepository)
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `when login success should return token`() = mainCoroutineRule.runTest {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Success(dummyLoginResponse)

        `when`(myStoryRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualLogin = loginViewModel.login(dummyEmail,dummyPassword).getOrAwaitValue()
        Mockito.verify(myStoryRepository).login(dummyEmail,dummyPassword)
        Assert.assertNotNull(actualLogin)
        Assert.assertTrue(actualLogin is Result.Success)
        Assert.assertEquals(dummyLoginResponse.loginResult?.token, (actualLogin as Result.Success).data.loginResult?.token )

    }

    @Test
    fun `when login failed should return Error`() = mainCoroutineRule.runTest {
        val expectedResponse = MutableLiveData<Result<LoginResponse>>()
        expectedResponse.value = Result.Error("Error")

        `when`(myStoryRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        val actualLogin = loginViewModel.login(dummyEmail,dummyPassword).getOrAwaitValue()
        Mockito.verify(myStoryRepository).login(dummyEmail,dummyPassword)
        Assert.assertNotNull(actualLogin)
        Assert.assertTrue(actualLogin is Result.Error)
    }

    @Test
    fun `Save authentication token successfully`(): Unit = runTest {
        loginViewModel.saveAuthToken(dummyToken)
        Mockito.verify(myStoryRepository).saveAuthToken(dummyToken)
    }
}