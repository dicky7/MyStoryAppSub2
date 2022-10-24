package com.example.mystoryapp.ui.onBoarding.splashScreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mystoryapp.MainActivity
import com.example.mystoryapp.databinding.FragmentSplashScreenBinding
import com.example.mystoryapp.ui.home.HomeActivity
import com.example.mystoryapp.utlis.ViewModelFactory
import kotlinx.coroutines.flow.collect


class SplashScreenFragment : Fragment() {

    private lateinit var viewModel: SplashScreeViewModel
    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setupActionBar()
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginFragment())
        }

    }


    @Suppress("DEPRECATION")
    private fun setupActionBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        (activity as AppCompatActivity).supportActionBar?.hide()
    }
}