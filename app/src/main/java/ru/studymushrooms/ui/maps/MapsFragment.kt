package ru.studymushrooms.ui.maps


import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.studymushrooms.App
import ru.studymushrooms.R
import ru.studymushrooms.api.PlaceModel
import ru.studymushrooms.service_locator.ServiceLocator

private const val PREFS_NAME = "ru.studymushrooms.prefs"
private const val PREFS_TILE_SOURCE = "tilesource"
private const val PREFS_LATITUDE_STRING = "latitudeString"
private const val PREFS_LONGITUDE_STRING = "longitudeString"
private const val PREFS_ORIENTATION = "orientation"
private const val PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble"

private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

class MapsFragment : Fragment(R.layout.fragment_maps) {
    @Suppress("UNCHECKED_CAST")
    private val viewModel: MapsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapsViewModel(ServiceLocator.placesRepository) as T
            }
        }
    }

    private lateinit var mapView: MapView
    private lateinit var prefs: SharedPreferences
    private lateinit var locationOverlay: MyLocationNewOverlay

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        mapView = view.findViewById(R.id.map_view)
        mapView.isTilesScaledToDpi = true
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        requestPermissionsIfNecessary(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        mapView.setMultiTouchControls(true)

        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)

        val rotationGestureOverlay = RotationGestureOverlay(mapView)
        rotationGestureOverlay.isEnabled = true
        mapView.setMultiTouchControls(true)
        mapView.overlays.add(rotationGestureOverlay)

        val compassOverlay =
            CompassOverlay(
                requireContext(),
                InternalCompassOrientationProvider(requireContext()),
                mapView
            )
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)

        viewModel.places.observe(viewLifecycleOwner) {
            showPlaces(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val zoomLevel = prefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 1f)
        mapView.controller.setZoom(zoomLevel.toDouble())

        val orientation = prefs.getFloat(PREFS_ORIENTATION, 0f)
        mapView.setMapOrientation(orientation, false)

        val latitudeString = prefs.getString(PREFS_LATITUDE_STRING, "1.0")!!
        val longitudeString = prefs.getString(PREFS_LONGITUDE_STRING, "1.0")!!

        val latitude = latitudeString.toDouble()
        val longitude = longitudeString.toDouble()
        mapView.setExpectedCenter(GeoPoint(latitude, longitude))
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
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onPause() {
        with(prefs.edit()) {
            putString(PREFS_TILE_SOURCE, mapView.tileProvider.tileSource.name())
            putFloat(PREFS_ORIENTATION, mapView.mapOrientation)
            putString(
                PREFS_LATITUDE_STRING,
                mapView.mapCenter.latitude.toString()
            )
            putString(
                PREFS_LONGITUDE_STRING,
                mapView.mapCenter.longitude.toString()
            )
            putFloat(PREFS_ZOOM_LEVEL_DOUBLE, mapView.zoomLevelDouble.toFloat())
            apply()
        }

        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        val tileSourceName = prefs.getString(
            PREFS_TILE_SOURCE,
            TileSourceFactory.DEFAULT_TILE_SOURCE.name()
        )

        try {
            val tileSource: ITileSource = TileSourceFactory.getTileSource(tileSourceName)
            mapView.setTileSource(tileSource)
        } catch (e: IllegalArgumentException) {
            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        }

        cleanMapMarkers()
        viewModel.getPlaces()
        mapView.onResume()
    }

    private fun cleanMapMarkers() {
        val toRemove = mutableListOf<Marker>()
        for (overlay in mapView.overlays) {
            if (overlay is Marker) {
                toRemove.add(overlay)
            }
        }
        mapView.overlays.removeAll(toRemove)
    }

    private fun showPlaces(places: List<PlaceModel>) {
        for (place in places) {
            val marker = object : Target, Marker(mapView) {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    image = placeHolderDrawable
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    image = errorDrawable
                }

                override fun onBitmapLoaded(
                    bitmap: Bitmap?,
                    from: Picasso.LoadedFrom?
                ) {
                    image = BitmapDrawable(resources, bitmap)
                }
            }

            // TODO: fix this in mapping
            Picasso.get().load(ServiceLocator.baseUrl + place.rawImage).into(marker)

            marker.position =
                GeoPoint(
                    place.latitude!!,
                    place.longitude!!
                )
            marker.title = "Найден " + place.date

            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }
}
