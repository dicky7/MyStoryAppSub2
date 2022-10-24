package com.example.mystoryapp.ui.onBoarding.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mystoryapp.MainActivity
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.databinding.FragmentLoginBinding
import com.example.mystoryapp.ui.home.HomeActivity
import com.example.mystoryapp.utlis.ViewModelFactory
import com.example.mystoryapp.utlis.animateVisibility
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {


    private lateinit var viewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        playAnimation()
        action()
    }

    /**
     * for setup actionBar fullScreen
     */
    private fun setupActionBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            @Suppress("DEPRECATION")
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    /**
     * setup viewModel before used
     */
    private fun setViewModel(){
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))[LoginViewModel::class.java]
    }

    /**
     * action function to handle input from user
     */
    private fun action(){
        binding.loginButton.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            when{
                email.isEmpty()->{
                    binding.edtEmail.error = getString(R.string.error_email)
                }
                password.isEmpty()->{
                    binding.edtPassword.error = getString(R.string.error_pasword)
                }
                else->{
                    lifecycleScope.launch {
                        viewModel.login(email,password).observe(viewLifecycleOwner){result->
                            loginResult(result)
                        }

                    }
                }
            }
        }

        binding.signInToSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    /**
     * Set Result for Login
     *
     * @param Resource<UserLoginModel>
     * @return Unit
     */
    private fun loginResult(result: Result<LoginResponse>){
        when(result){
            is Result.Loading->{
                showProgressBarUser(true)
            }
            is Result.Success->{
                showProgressBarUser(false)
                //getToken from login
                result.data.loginResult?.token.let {token->
                    viewModel.saveAuthToken(
                        token ?: "",
                    )
                }
                //alertDialog
                AlertDialog.Builder(context).apply {
                    setTitle(getString(R.string.success))
                    setMessage(getString(R.string.desc_success_login))
                    setPositiveButton(getString(R.string.ok)) { _, _ ->
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        activity?.finish()
                    }
                    create()
                    show()
                }

            }
            is  Result.Error->{
                showProgressBarUser(false)
                Snackbar.make(
                    binding.root,
                    result.error,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Determine loading indicator is visible or not
     *
     * @return Boolean
     */
    private fun showProgressBarUser(state: Boolean) {
        with(binding){
            loginButton.isEnabled = !state
            edtPassword.isEnabled = !state
            edtEmail.isEnabled = !state
            loadingView.animateVisibility(state)
            progressBar.isVisible = state
        }
    }

    /**
     * to handle animation in login
     */
    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val titleTextView = ObjectAnimator.ofFloat(binding.titleSignIn, View.ALPHA, 1f).setDuration(500)
        val messageTextView = ObjectAnimator.ofFloat(binding.descSignIn, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val loginButton = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val signToSignUp = ObjectAnimator.ofFloat(binding.signInToSignUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                titleTextView,
                messageTextView,
                emailEditTextLayout,
                passwordEditTextLayout,
                loginButton,
                signToSignUp
            )
            start()
        }
    }

    /**
     * destroy fragment
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}