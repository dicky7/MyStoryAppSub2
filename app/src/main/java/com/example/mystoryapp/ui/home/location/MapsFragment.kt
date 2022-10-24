package com.example.mystoryapp.ui.home.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.databinding.FragmentMapsBinding
import com.example.mystoryapp.utlis.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class MapsFragment : Fragment() {
    companion object{
        const val ARGUMENT_TO_PICK_LOCATION = 1
        const val REQUEST_KEY_MAPS = "request_key_maps"
        const val LATNG_MAPS_RESULT = "latng_maps_result"
    }

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val args: MapsFragmentArgs by navArgs()
    private lateinit var viewModel: MapViewModel
    private lateinit var mMap: GoogleMap

    private var token =""


    /**
     * callback to show story marker in map
     */
    private val callbackStories = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = googleMap
        //add control
        mMap.uiSettings.isZoomControlsEnabled = true
        val jakarta = LatLng(-6.200000, 106.816666)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jakarta, 5f))
        setMapStyle()
        viewModelGetStory()
        onBackPressed()
    }

    /**
     * callback to pick current address with custom marker
     */
    private val callbackPickLocation = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = googleMap
        //add control
        mMap.uiSettings.isZoomControlsEnabled = true
        getMyLocation()
        setMapStyle()
        onBackPressed()

        //get current position with custom marker
        with(binding){
            var currentPosition = mMap.cameraPosition.target
            mMap.setOnCameraMoveStartedListener{
                marker.animate().translationY(-25f).start()
                markerShadow.animate().scaleX(0.5f).scaleY(0.5f).start()
            }
            mMap.setOnCameraIdleListener {
                marker.animate().translationY(0f).start()
                markerShadow.animate().scaleX(1f).scaleY(1f).start()
                currentPosition = mMap.cameraPosition.target
            }

            //In fragment maps, the fragment producing the result, you must set the result on the same FragmentManager by using the same REQUEST_KEY_MAPS.
            //LATLNG is key to get current location
            btnSaveMaps.setOnClickListener {
                setFragmentResult(REQUEST_KEY_MAPS, Bundle().apply { putParcelable(LATNG_MAPS_RESULT,currentPosition) })
                findNavController().popBackStack()
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        //set args when equals with ARGUMENT_TO_PICK_LOCATION, which is mean when map fragment open from addStory
        //show custom marker and save current location
        val action = args.actionMaps    //getArgument navigation
        if (action == ARGUMENT_TO_PICK_LOCATION){
            with(binding){
                marker.isVisible = true
                markerShadow.isVisible = true
                btnSaveMaps.isVisible = true
                mapFragment?.getMapAsync(callbackPickLocation)
                return
            }
        }
        //call the callback to to show map stories
        setViewModel()
        mapFragment?.getMapAsync(callbackStories)


    }

    /**
     * Get device location and request location permission if still doesn't have any location permission
     */
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /**
     * setup viewModel before used
     */
    private fun setViewModel(){
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))[MapViewModel::class.java]
        viewModelGetToken()
    }
    /**
     * function to handle viewModel getStories and token login
     */
    private fun viewModelGetToken(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAuthToken().observe(viewLifecycleOwner) {
                token = it
            }
        }
    }

    /**
     * getListStory from api
     *
     * @param result: Result<List<ListStoryItem>>
     * @return Unit
     */
    private fun viewModelGetStory(){
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                viewModel.getStoriesLocation(token, null,30).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Success -> {
                            result.data.listStory.forEach { listStoryItem ->
                                if (listStoryItem.lat != null && listStoryItem.lon != null) {
                                    val latLang = LatLng(listStoryItem.lat, listStoryItem.lon)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(latLang)
                                            .title(listStoryItem.name)
                                            .snippet(listStoryItem.description)
                                    )
                                }
                            }
                        }
                        is Result.Error -> {
                            Snackbar.make(
                                binding.root,
                                result.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * apply custom map style
     */
    private fun setMapStyle(){
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if (!success) {
                Log.e("TAG", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("TAG", "Can't find style. Error: ", exception)
        }
    }

    /**
     * set when icon back clicked
     */
    private fun onBackPressed(){
        binding.backMaps.setOnClickListener { findNavController().popBackStack() }
    }

}