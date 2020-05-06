package ru.studymushrooms.ui.recognize

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cocoahero.android.geojson.Point
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.MainActivity
import ru.studymushrooms.R
import ru.studymushrooms.api.ImageRequest
import ru.studymushrooms.api.PlaceModel
import ru.studymushrooms.api.RecognitionModel
import java.io.ByteArrayOutputStream


class RecognitionFragment : Fragment() {

    private lateinit var recognitionViewModel: RecognitionViewModel
    private val STORAGE_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2
    private lateinit var textView: TextView
    private lateinit var imageView: ImageView
    private val adapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recognitionViewModel =
            ViewModelProviders.of(this).get(RecognitionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_recognition, container, false)
        return root
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
        view.findViewById<Button>(R.id.recognize_storage_button).setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, STORAGE_REQUEST_CODE)
        }
        view.findViewById<Button>(R.id.recognize_photo_button).setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        }

        recyclerView = view.findViewById(R.id.recognize_recyclerview)
        textView = view.findViewById(R.id.recognize_textview)
        imageView = view.findViewById(R.id.recognize_imageview)
    }

    fun getDegrees(exifRes: String?, exifRef: String?): Double? {
        if (exifRef == null || exifRes == null) return null
        val a = exifRes.split(",").map {
            val k = it.split('/').map { it.toDouble() }
            return@map k[0] / k[1]
        }
        return (a[0] + a[1] / 60 + a[2] / 3600) * if (exifRef in "SW") -1 else 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            adapter.clear()
            val b64Image: String
            val image: Bitmap
            var exif: ExifInterface? = null
            if (requestCode == CAMERA_REQUEST_CODE) {

                image = data?.extras?.get("data") as Bitmap
                val baos = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                b64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            } else {
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)

                val cursor: Cursor? = requireActivity().contentResolver.query(
                    data!!.data!!,
                    filePathColumn, null, null, null
                )
                cursor!!.moveToFirst()

                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                val picturePath: String = cursor.getString(columnIndex)
                cursor.close()
                exif = ExifInterface(picturePath)
                image = BitmapFactory.decodeFile(picturePath)
                val baos = ByteArrayOutputStream()

                image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                b64Image = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            }
            val x = getDegrees(
                exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),
                exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
            )
            val y = getDegrees(
                exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE),
                exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
            )

            val exifLocation = if (exif == null || x == null || y == null) null else Point(
                x,
                y
            )

            App.api.recognize(
                App.token!!, ImageRequest(b64Image)
            ).enqueue(object :
                Callback<List<RecognitionModel>> {
                override fun onFailure(call: Call<List<RecognitionModel>>, t: Throwable) {
                    Log.e("Recognition", t.message)
                }

                override fun onResponse(
                    call: Call<List<RecognitionModel>>,
                    response: Response<List<RecognitionModel>>
                ) {
                    if (response.isSuccessful) {
                        for (i in response.body()!!)
                            adapter.add(RecognitionItem(i))
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager =
                            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        recyclerView.visibility = View.VISIBLE
                        textView.visibility = View.VISIBLE
                        imageView.visibility = View.VISIBLE
                        imageView.setImageDrawable(BitmapDrawable(resources, image))
                        var useLocation = true
                        val alert = MaterialAlertDialogBuilder(context).setTitle("Запомнить место?")
                            .setPositiveButton("Да") { dialog, which ->
                                val location = (requireActivity() as MainActivity).location
                                val call = App.api.addPlace(
                                    App.token!!, PlaceModel(
                                        rawImage = b64Image,
                                        location = if (useLocation && location != null) Point(
                                            location.longitude,
                                            location.latitude
                                        ) else if (exifLocation != null) exifLocation else
                                            null,
                                        date = null,
                                        useLocation = exifLocation != null || location != null,
                                        longitude = null,
                                        latitude = null
                                    )
                                )
                                call.enqueue(object : Callback<ResponseBody> {
                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Toast.makeText(
                                            context,
                                            "Что-то пошло не так, место не добавлено",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        if (response.isSuccessful)
                                            Toast.makeText(
                                                context,
                                                "Место добавлено",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        else
                                            Toast.makeText(
                                                context,
                                                "Что-то пошло не так, место не добавлено",
                                                Toast.LENGTH_LONG
                                            ).show()
                                    }

                                })
                            }.setNeutralButton("Отмена") { dialog, which ->
                                dialog.dismiss()
                            }
                        if (requestCode == STORAGE_REQUEST_CODE)
                            alert.setSingleChoiceItems(
                                arrayOf(
                                    "Использовтаь текущую геолокацию",
                                    "Использовать данные о геолокации картинки"
                                ), 0
                            ) { dialog, which ->
                                useLocation = which != 1
                            }
                        alert.show()
                    } else
                        Log.d("Recognition", response.errorBody().toString())
                }

            })
            requireActivity().getSystemService(Context.LOCATION_SERVICE)
        }
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

