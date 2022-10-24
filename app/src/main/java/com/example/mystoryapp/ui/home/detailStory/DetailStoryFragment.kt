package com.example.mystoryapp.ui.home.detailStory

import android.os.Bundle
import android.os.TransactionTooLargeException
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mystoryapp.R
import com.example.mystoryapp.data.local.entity.StoryEntity
import com.example.mystoryapp.data.remote.response.ListStoryItem
import com.example.mystoryapp.databinding.FragmentDetailStoryBinding
import com.example.mystoryapp.utlis.generateDateFormat

class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailStoryBinding.inflate(layoutInflater, container, false)
        setupActionBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.supportPostponeEnterTransition()
        val detailStory = DetailStoryFragmentArgs.fromBundle(arguments as Bundle).storyDetailParcelable
        parsingStory(detailStory)

    }

    /**
     * for setup actionBar fullScreen
     */
    private fun setupActionBar(){
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
    }

    /**
     * Parsing User data to it's view
     *
     * @param listStoryItem
     * @return Unit
     */
    private fun parsingStory(listStoryItem: StoryEntity?){
        if (listStoryItem != null){
            with(binding){
                storyUsername.text = listStoryItem.name
                storyDatePost.generateDateFormat(listStoryItem.createdAt)
                storyDesc.text = listStoryItem.description
                context?.let {
                    Glide.with(it)
                        .load(listStoryItem.photoUrl)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                        .into(storyImage)
                }

                //set current detail binding with transitionName
                storyUsername.transitionName = listStoryItem.name
                storyDesc.transitionName = listStoryItem.description
                storyImage.transitionName = listStoryItem.photoUrl
                storyyUserAvatar.transitionName = "avatar_profile"

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}