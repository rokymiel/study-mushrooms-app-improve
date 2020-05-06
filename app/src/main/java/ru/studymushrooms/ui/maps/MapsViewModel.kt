package ru.studymushrooms.ui.maps

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.studymushrooms.App
import ru.studymushrooms.api.PlaceModel

class MapsViewModel : ViewModel() {

    fun getPlaces(map: MapView, resources: Resources) {
        val call = App.api.getPlaces(App.token!!)
        for (i in map.overlays)
            if (i is Marker)
                map.overlays.remove(i)

        call.enqueue(object : Callback<List<PlaceModel>> {
            override fun onResponse(
                call: Call<List<PlaceModel>>,
                response: Response<List<PlaceModel>>
            ) {
                if (response.isSuccessful) {
                    for (i in response.body()!!) {
                        val m = object : Target, Marker(map) {
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

                        Picasso.get().load(App.baseUrl + i.rawImage).into(m)

                        m.position =
                            GeoPoint(
                                i.latitude!!,
                                i.longitude!!
                            )
                        m.title = "Найден " + i.date

                        map.overlays.add(m)
                    }
                    map.invalidate()
                }
            }

            override fun onFailure(call: Call<List<PlaceModel>>, t: Throwable) {
                throw t
            }
        })
    }

}