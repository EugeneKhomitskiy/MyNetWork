package com.example.mynetwork.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.example.mynetwork.databinding.FragmentNewEventBinding
import com.example.mynetwork.extension.formatToInstant
import com.example.mynetwork.util.AndroidUtils
import com.example.mynetwork.viewmodel.EventViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewEventFragment : Fragment() {

    private val calendar = Calendar.getInstance()

    private var fragmentBinding: FragmentNewEventBinding? = null

    private val eventViewModel: EventViewModel by activityViewModels()

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
                            formatToInstant("${it.editDate.text} ${it.editTime.text}")
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        )

        val datePicker = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = monthOfYear
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            binding.editDate.setText(
                SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                    .format(calendar.time)
            )
        }

        val timePicker = OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            binding.editTime.setText(
                SimpleDateFormat("HH-mm", Locale.ROOT)
                    .format(calendar.time)
            )
        }

        binding.date.setOnClickListener {
            context?.let {
                DatePickerDialog(
                    it, datePicker,
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                    .show()
            }
        }

        binding.time.setOnClickListener {
            TimePickerDialog(
                context, timePicker,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true
            )
                .show()
        }

        fragmentBinding = binding

        binding.removePhoto.setOnClickListener {
            eventViewModel.changePhoto(null)
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        eventViewModel.changePhoto(uri)
                    }
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
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removePhoto.setOnClickListener {
            eventViewModel.changePhoto(null)
        }

        eventViewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }
            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        eventViewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}