package com.example.mystoryapp.ui.home.addStory

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.remote.response.UploadStoryResponse
import com.example.mystoryapp.databinding.FragmentAddStoryBinding
import com.example.mystoryapp.ui.home.HomeActivity
import com.example.mystoryapp.ui.home.location.MapsFragment.Companion.ARGUMENT_TO_PICK_LOCATION
import com.example.mystoryapp.ui.home.location.MapsFragment.Companion.LATNG_MAPS_RESULT
import com.example.mystoryapp.ui.home.location.MapsFragment.Companion.REQUEST_KEY_MAPS
import com.example.mystoryapp.utlis.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryFragment : Fragment(), ImagePickerFragment.OnConfirmListener {

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_PERMISSION_CODE = 10
    }

    private lateinit var viewModel: AddStoryViewModel
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var mImagePickerFragment: ImagePickerFragment

    //camera
    private lateinit var currentPhotoPath: String
    //getFile
    private var getFile: File? = null
    private var token = ""
    private var locationLat = 0.0F
    private var locationLng = 0.0F


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddStoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (!allPermissionGranted()) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.permission),
                    Snackbar.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this.requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_PERMISSION_CODE
            )
        }
        setupActionBar()
        setViewModel()
        action()
        onBackPressed()
    }


    /**
     * to show custom menu to action bar
     */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * handle action when menu selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.add_menu->{
                uploadStories()
                true
            }
            android.R.id.home-> {
                backPressAlertDialog()
                true
            }
            else->{
                super.onOptionsItemSelected(item)
            }
        }

    }

    /**
     * for setup actionBar fullScreen
     */
    private fun setupActionBar(){
        setHasOptionsMenu(true) // Add this!
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        binding.toolbar.navigationIcon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_baseline_arrow_back_ios_24) }
        binding.toolbar.setNavigationOnClickListener { backPressAlertDialog() }
        (activity as AppCompatActivity).supportActionBar?.let { it.title = "Add Story" }
    }

    /**
     * action function to handle input from user
     */
    private fun action(){
        with(binding){
            pickImage.setOnClickListener {
                getImagePicker()
            }
            tvChangeCurrentLocation.setOnClickListener {
                val toMapsFragment = AddStoryFragmentDirections.actionAddStoryFragmentToMapsFragment()
                toMapsFragment.actionMaps = ARGUMENT_TO_PICK_LOCATION
                findNavController().navigate(toMapsFragment)
            }

            //To pass data back to fragment A from fragment B, first set a result listener on fragment A, the fragment that receives the result.
            // Call setFragmentResultListener() with KEY, make sure use same KEY from fragment B to receives the result
            setFragmentResultListener(REQUEST_KEY_MAPS){_,storyBundle->
                val locationLatLng = storyBundle.getParcelable(LATNG_MAPS_RESULT) as LatLng?
                if (locationLatLng != null){
                    tvShowCurrentLocation.text = getString(R.string.current_location, locationLatLng.latitude.toString(), locationLatLng.longitude.toString())

                    locationLat = locationLatLng.latitude.toFloat()
                    locationLng = locationLatLng.longitude.toFloat()
                }

            }

        }
    }

    /**
     * set location
     */

    /**
     * setup viewModel before used
     */
    private fun setViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext()))[AddStoryViewModel::class.java]
        lifecycleScope.launch {
            viewModel.getAuthToken().observe(viewLifecycleOwner){
                token = it
            }
        }
    }

    /**
     * upload user story to the server and checking data is valid
     */
    private fun uploadStories(){
        with(binding) {
            //check data is empty or not
            showProgressBar(true)
            var isDataValid = true
            val description = edtDescription.text.toString()

            //if description is empty return false and show an error message
            if (description.isEmpty()){
                edtDescription.error = getString(R.string.error_description)
                isDataValid = false
            }

            //if file or image is empty return false and show an error message
            if (getFile==null){
                Snackbar.make(binding.root, getString(R.string.error_pick_image), Snackbar.LENGTH_SHORT).show()
                isDataValid = false
            }

            if (isDataValid){
                val file = reduceFile(getFile as File)
                val descriptionStory = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                lifecycleScope.launchWhenCreated{
                    launch {
                        viewModel.uploadStories(token, imageMultipart, descriptionStory, locationLat, locationLng).observe(viewLifecycleOwner){result->
                            resultUploadStories(result)
                        }
                    }
                }
            }else showProgressBar(false)
        }
    }

    /**
     * Set Result for UploadStories
     *
     * @param Result<UploadStoryResponse>
     * @return Unit
     */
    private fun resultUploadStories(result: Result<UploadStoryResponse>){
        when(result){
            is Result.Loading->{
                showProgressBar(true)
            }
            is Result.Success->{
                showProgressBar(false)
                AlertDialog.Builder(context).apply {
                    setTitle(getString(R.string.success))
                    setMessage(getString(R.string.desc_success_upload_story))
                    setPositiveButton(getString(R.string.ok)){_,_->
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        activity?.finish()
                    }
                    create()
                    show()
                }
            }
            is Result.Error->{
                showProgressBar(false)
                Snackbar.make(
                    binding.root,
                    result.error,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }


    /**
     * for intialize image picker
     */
    private fun getImagePicker(){
        when (::mImagePickerFragment.isInitialized) {
            false-> {
                mImagePickerFragment = ImagePickerFragment.newInstance()
                mImagePickerFragment.setItemClickCallBack(this@AddStoryFragment)
            }
        }
        when(!mImagePickerFragment.isAdded){
            true-> mImagePickerFragment.show(parentFragmentManager, mImagePickerFragment.javaClass.simpleName)
        }
    }

    /**
     * function for take photo using camera
     */
    fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        createCustomTempFile(requireActivity().application).also {
            val photoUri : Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.mystoryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    fun startIntentGallery(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture_gallery))
        launcherIntentGallery.launch(chooser)
    }


    /**
     * untuk menampung hasil dari intent atau IntentResult camera
     */
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)

            getFile = myFile
            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.addPreviewImage.setImageBitmap(result)
        }
    }

    /**
     * untuk menampung hasil dari intent atau IntentResult camera
     */
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == AppCompatActivity.RESULT_OK){
            //create temporary to collect image from gallery
            val selectedImageUri = it.data?.data as Uri

            // then using stream to write what we got from Uri (file gallery) into myFile.
            val myFile = uriToFile(selectedImageUri, requireContext())

            getFile = myFile
            binding.addPreviewImage.setImageURI(selectedImageUri)
        }
    }


    /**
     * Determine loading indicator is visible or not
     * @param state
     */
    private fun showProgressBar(state:Boolean){
        with(binding){
            progressBar.isVisible = state
            pickImage.isEnabled != state
            loadingView.animateVisibility(state)
            edtDescription.isEnabled != state
        }
    }

    /**
     * function to create alert dialog when backpressed
     */
    private fun backPressAlertDialog(){
        val alertDialogBuilder = AlertDialog.Builder(context)
        with(alertDialogBuilder){
            setTitle(getString(R.string.cancel))
            setMessage(getString(R.string.message_cancel))
            setPositiveButton(getString(R.string.ok)){ _, _->
                 findNavController().popBackStack()
            }
            setNegativeButton(getString(R.string.cancel)){ dialog, _->
                dialog.cancel()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun onBackPressed(){
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressAlertDialog()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onImagePick(action: Int) {
        when (action) {
            1 -> startTakePhoto()
            2 -> startIntentGallery()
        }
    }

}