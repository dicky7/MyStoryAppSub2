package com.example.mystoryapp.ui.onBoarding.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mystoryapp.MainActivity
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.remote.response.LoginResponse
import com.example.mystoryapp.data.remote.response.RegisterResponse
import com.example.mystoryapp.databinding.FragmentLoginBinding
import com.example.mystoryapp.databinding.FragmentRegisterBinding
import com.example.mystoryapp.ui.onBoarding.login.LoginFragmentDirections
import com.example.mystoryapp.ui.onBoarding.login.LoginViewModel
import com.example.mystoryapp.utlis.ViewModelFactory
import com.example.mystoryapp.utlis.animateVisibility
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var viewModel: RegisterViewModel
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        playAnimation()
        action()
    }

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
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))[RegisterViewModel::class.java]
    }

    /**
     * action function to handle input from user
     */
    private fun action(){
        binding.signUpButton.setOnClickListener {
            val name = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            when{
                name.isEmpty()->{
                    binding.edtUsername.error = getString(R.string.error_username)
                }
                email.isEmpty()->{
                    binding.edtEmail.error = getString(R.string.error_email)
                }
                password.isEmpty()->{
                    binding.edtPassword.error = getString(R.string.error_pasword)
                }
                else->{
                    lifecycleScope.launch {
                        viewModel.register(name, email, password)
                            .observe(viewLifecycleOwner) { result ->
                                registerResult(result)
                            }
                    }
                }
            }
        }

        binding.signUpToSign.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }

    /**
     * Set Result for Register
     *
     * @param Resource<UserLoginModel>
     * @return Unit
     */
    private fun registerResult(result: Result<RegisterResponse>){
        when(result){
            is Result.Loading->{
                showProgressBar(true)
            }
            is Result.Success->{
                showProgressBar(false)
                //alertDialog
                AlertDialog.Builder(context).apply {
                    setTitle(getString(R.string.success))
                    setMessage(getString(R.string.desc_success_register))
                    setPositiveButton(getString(R.string.ok)) { _, _ ->
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                    }
                    create()
                    show()
                }

            }
            is  Result.Error->{
                showProgressBar(false)
                Snackbar.make(
                    binding.root,
                    result.error.toString(),
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
    private fun showProgressBar(state:Boolean){
        with(binding){
            signUpButton.isEnabled = !state
            edtEmail.isEnabled = !state
            edtPassword.isEnabled = !state
            loadingView.animateVisibility(state)
            progressBar.isVisible = state
        }
    }


    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleTextView = ObjectAnimator.ofFloat(binding.titleSignUp, View.ALPHA, 1f).setDuration(500)
        val descTextView = ObjectAnimator.ofFloat(binding.descSignUp, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.edtUsername, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val signupButton = ObjectAnimator.ofFloat(binding.signUpButton, View.ALPHA, 1f).setDuration(500)
        val signToSignUp = ObjectAnimator.ofFloat(binding.signUpToSign, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                titleTextView,
                descTextView,
                nameEditTextLayout,
                emailEditTextLayout,
                passwordEditTextLayout,
                signupButton,
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