package com.example.mynetwork.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.databinding.FragmentNewEventBinding
import com.example.mynetwork.dto.Coordinates
import com.example.mynetwork.dto.Event
import com.example.mynetwork.enumeration.AttachmentType
import com.example.mynetwork.extension.formatToDate
import com.example.mynetwork.extension.formatToInstant
import com.example.mynetwork.extension.pickDate
import com.example.mynetwork.extension.pickTime
import com.example.mynetwork.util.AndroidUtils
import com.example.mynetwork.viewmodel.EventViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewEventFragment : Fragment() {

    var type: AttachmentType? = null

    private var fragmentBinding: FragmentNewEventBinding? = null

    private val eventViewModel: EventViewModel by activityViewModels()

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
                        eventViewModel.change(
                            it.edit.text.toString(),
                            formatToInstant("${it.editDate.text} ${it.editTime.text}"),
                            Coordinates(latitude!!, longitude!!)
                        )
                        eventViewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        )

        (activity as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.title_event)

        fragmentBinding = binding

        latitude = arguments?.getDouble("lat")
        longitude = arguments?.getDouble("lng")

        val dateTime = arguments?.getString("dateTime")?.let { formatToDate(it) }
            ?: formatToDate("${eventViewModel.edited.value?.datetime}")

        val date = dateTime.substring(0, 10)
        val time = dateTime.substring(11)

        binding.edit.setText(
            arguments?.getString("content") ?: eventViewModel.edited.value?.content
        )
        if (eventViewModel.edited.value != Event.empty) {
            binding.editDate.setText(date)
            binding.editTime.setText(time)
        }

        binding.geoLabel.setOnClickListener {
            eventViewModel.change(
                binding.edit.text.toString(),
                formatToInstant("${binding.editDate.text} ${binding.editTime.text}"),
                null
            )
            val bundle = Bundle().apply { putString("open", "newEvent") }
            findNavController().navigate(R.id.mapFragment, bundle)
        }

        binding.editDate.setOnClickListener {
            context?.let { it1 -> it.pickDate(binding.editDate, it1) }
        }

        binding.editTime.setOnClickListener {
            context?.let { it1 -> it.pickTime(binding.editTime, it1) }
        }

        binding.speaker.setOnClickListener {
            val bundle = Bundle().apply { putString("open", "speaker") }
            findNavController().navigate(R.id.navigation_users, bundle)
        }

        eventViewModel.edited.observe(viewLifecycleOwner) {
            binding.speaker.apply {
                text = "$text ${eventViewModel.edited.value?.speakerIds?.count().toString()}"
            }
        }

        binding.removePhoto.setOnClickListener {
            eventViewModel.changeMedia(null, null, null)
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it.data?.data.let { uri ->
                    val stream = uri?.let { context?.contentResolver?.openInputStream(it) }
                    eventViewModel.changeMedia(uri, stream, type)
                }
            }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg"
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
            type = AttachmentType.IMAGE
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
            eventViewModel.changeMedia(null, null, null)
        }

        eventViewModel.media.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }
            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        eventViewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.navigation_events)
        }

        return binding.root
    }
}
