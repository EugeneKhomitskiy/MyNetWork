package com.example.mynetwork.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.databinding.FragmentNewPostBinding
import com.example.mynetwork.dto.Coordinates
import com.example.mynetwork.enumeration.AttachmentType
import com.example.mynetwork.util.AndroidUtils
import com.example.mynetwork.viewmodel.PostViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewPostFragment : Fragment() {

    var type: AttachmentType? = null

    private var fragmentBinding: FragmentNewPostBinding? = null

    private val postViewModel: PostViewModel by activityViewModels()

    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_post, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                fragmentBinding?.let {
                    if (it.edit.text.isNullOrBlank()) {
                        Toast.makeText(
                            activity,
                            R.string.error_empty_content,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        postViewModel.change(
                            it.edit.text.toString(),
                            Coordinates(latitude, longitude)
                        )
                        postViewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        (activity as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.title_post)

        fragmentBinding = binding

        latitude = arguments?.getDouble("lat")
        longitude = arguments?.getDouble("lng")

        binding.edit.setText(
            arguments?.getString("content") ?: postViewModel.edited.value?.content
        )

        binding.geoLabel.setOnClickListener {
            postViewModel.change(
                binding.edit.text.toString(),
                null
            )
            val bundle = Bundle().apply { putString("open", "newPost") }
            findNavController().navigate(R.id.mapFragment, bundle)
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it.data?.data.let { uri ->
                    val stream = uri?.let { context?.contentResolver?.openInputStream(it) }
                    postViewModel.changeMedia(uri, stream, type)
                }
            }

        val pickMediaLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    val stream = context?.contentResolver?.openInputStream(it)
                    postViewModel.changeMedia(it, stream, type)
                }
            }

        binding.pickPhoto.setOnClickListener {
            pickMediaLauncher.launch("image/*")
            type = AttachmentType.IMAGE
        }

        binding.pickAudio.setOnClickListener {
            pickMediaLauncher.launch("audio/*")
            type = AttachmentType.AUDIO
        }

        binding.pickVideo.setOnClickListener {
            pickMediaLauncher.launch("video/*")
            type = AttachmentType.VIDEO
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
            type = AttachmentType.IMAGE
        }

        binding.removePhoto.setOnClickListener {
            postViewModel.changeMedia(null, null, null)
        }

        postViewModel.media.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.mediaContainer.visibility = View.GONE
                return@observe
            }
            binding.info.text = postViewModel.media.value?.uri?.path
            binding.mediaContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        postViewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.navigation_posts)
        }

        postViewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT).show()
                }
            }
            binding.progress.isVisible = it.loading
        }

        return binding.root
    }
}