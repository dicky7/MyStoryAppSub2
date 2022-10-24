package com.example.mystoryapp.ui.home.addStory

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.mystoryapp.MainActivity
import com.example.mystoryapp.R
import com.example.mystoryapp.databinding.FragmentImagePickerBinding
import org.koin.android.ext.android.bind


class ImagePickerFragment : DialogFragment(){
    companion object{
        fun newInstance() = ImagePickerFragment()
    }

    private var _binding: FragmentImagePickerBinding? = null
    private val binding get() = _binding!!
    private lateinit var itemClickCallBack: OnConfirmListener
    fun setItemClickCallBack(onConfirmListener: OnConfirmListener){
        this.itemClickCallBack = onConfirmListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImagePickerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        action()
    }

    private fun action(){
        binding.btCamera.setOnClickListener {
            itemClickCallBack.onImagePick(1)
            dismiss()
        }
        binding.btGallery.setOnClickListener {
            itemClickCallBack.onImagePick(2)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)
        dialog.apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return dialog
    }


    interface OnConfirmListener{
        fun onImagePick(action: Int)
    }

}