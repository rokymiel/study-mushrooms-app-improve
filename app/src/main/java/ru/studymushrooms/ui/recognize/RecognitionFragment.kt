package ru.studymushrooms.ui.recognize

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cocoahero.android.geojson.Point
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ru.studymushrooms.MainActivity
import ru.studymushrooms.R
import ru.studymushrooms.service_locator.ServiceLocator
import ru.studymushrooms.ui.notes.NotesViewModel


class RecognitionFragment : Fragment(R.layout.fragment_recognition) {
    @Suppress("UNCHECKED_CAST")
    private val recognitionViewModel: RecognitionViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecognitionViewModel(ServiceLocator.recognitionRepository) as T
            }
        }
    }

    private lateinit var recognizeStorageButton: Button
    private lateinit var recognizePhotoButton: Button

    private lateinit var textView: TextView
    private lateinit var imageView: ImageView

    private val adapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    private lateinit var recyclerView: RecyclerView

    private var uri: Uri = Uri.EMPTY
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                uri
                handleImage(uri, false)
            }
        }

    private val getContentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            handleImage(it, true)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissionsIfNecessary(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissionsIfNecessary(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        }

        recognizeStorageButton = view.findViewById(R.id.recognize_storage_button)
        recognizeStorageButton.setOnClickListener {
            getContentLauncher.launch("image/*")
        }

        recognizePhotoButton = view.findViewById(R.id.recognize_photo_button)
        recognizePhotoButton.setOnClickListener {
            takePictureLauncher.launch(uri)
        }

        recyclerView = view.findViewById(R.id.recognize_recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        textView = view.findViewById(R.id.recognize_textview)
        imageView = view.findViewById(R.id.recognize_imageview)

        recognitionViewModel.recognitions.observe(viewLifecycleOwner) {
            adapter.addAll(it)
            recyclerView.isVisible = true
            textView.isVisible = true
        }

        recognitionViewModel.recognitionEvents.observe(viewLifecycleOwner) {
            when (it) {
                is RecognitionEvents.ShowLocationDialog -> showLocationDialog(it)
                is RecognitionEvents.ShowRecognitionResult -> showRecognitionResult(it.result)
            }
        }

        recognitionViewModel.mushroomImage.observe(viewLifecycleOwner) {
            imageView.isVisible = true
            imageView.setImageBitmap(it)
        }
    }

    private fun showLocationDialog(showLocationDialogEvent: RecognitionEvents.ShowLocationDialog) {
        var useLocation = true
        val alert = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.remember_place_location))
            .setPositiveButton(getString(R.string.confirm_button_text)) { _, _ ->
                val globalLocation = (requireActivity() as MainActivity).location
                val location = if (useLocation && globalLocation != null) {
                    Point(globalLocation.longitude, globalLocation.latitude)
                } else {
                    showLocationDialogEvent.location
                }
                recognitionViewModel.savePlace(location, showLocationDialogEvent.b64Image)
            }
            .setNeutralButton(getString(R.string.dismiss_button_text)) { dialog, _ ->
                dialog.dismiss()
            }
        if (showLocationDialogEvent.showOptions) {
            alert.setSingleChoiceItems(
                arrayOf(
                    getString(R.string.use_current_location),
                    getString(R.string.use_picture_location)
                ), 0
            ) { _, which ->
                useLocation = which != 1
            }
        }
        alert.show()
    }

    private fun showRecognitionResult(result: RecognitionRequestResult) {
        val text = getString(result.res)
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }

    private fun getDegrees(exifRes: String?, exifRef: String?): Double? {
        if (exifRef == null || exifRes == null) return null
        val a = exifRes.split(",").map {
            val k = it.split('/').map { it.toDouble() }
            k[0] / k[1]
        }
        return (a[0] + a[1] / 60 + a[2] / 3600) * if (exifRef in "SW") -1 else 1
    }

    private fun handleImage(uri: Uri, isFromStorage: Boolean) {
        adapter.clear()

        val contentResolver = requireActivity().contentResolver

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        var x: Double? = null
        var y: Double? = null

        contentResolver.openInputStream(uri).use { inputStream ->
            inputStream?.let {
                val exifInterface = ExifInterface(it)
                x = getDegrees(
                    exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),
                    exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
                )
                y = getDegrees(
                    exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE),
                    exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
                )
            }
        }

        val xCoord = x
        val yCoord = y

        val point = if (xCoord != null && yCoord != null) {
            Point(xCoord, yCoord)
        } else {
            null
        }

        recognitionViewModel.recognize(bitmap, isFromStorage, point)
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toArray(arrayOfNulls(0)),
                1
            )
        }
    }
}

