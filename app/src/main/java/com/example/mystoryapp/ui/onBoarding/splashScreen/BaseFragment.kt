package com.example.mystoryapp.ui.onBoarding.splashScreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mystoryapp.MainActivity
import com.example.mystoryapp.R
import com.example.mystoryapp.databinding.FragmentBaseBinding
import com.example.mystoryapp.ui.home.HomeActivity
import com.example.mystoryapp.utlis.ViewModelFactory
import kotlinx.coroutines.flow.collect


class BaseFragment : Fragment() {

    private var _binding: FragmentBaseBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: SplashScreeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setupActionBar()
        _binding = FragmentBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Handler(Looper.getMainLooper()).postDelayed({
            setViewModel()
        }, 200)
    }

    /**
     * setup viewModel before used and set when user is loggedIn
     */
    private fun setViewModel(){
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))[SplashScreeViewModel::class.java]
        lifecycleScope.launchWhenCreated {
            viewModel.isLoggedIn().observe(viewLifecycleOwner){isLoggedIn->
                if (isLoggedIn){
                    val intent = Intent(context, HomeActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                else{
                    findNavController().navigate(BaseFragmentDirections.actionBaseFragmentToSplashScreenFragment())
                }
            }
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