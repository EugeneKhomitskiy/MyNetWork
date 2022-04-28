package com.example.mynetwork.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mynetwork.R
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.ui_view.ViewProvider

class MapFragment : Fragment(), LocationListener, InputListener {

    private lateinit var mapView: MapView
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var marksObject: MapObjectCollection
    private lateinit var locationManager: LocationManager

    private var position: Point? = null
    private var open: String? = null

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                mapView.apply {
                    userLocationLayer.isVisible = true
                    userLocationLayer.isHeadingEnabled = false
                }
            } else {
                Toast.makeText(context, R.string.permission_deny, Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        (activity as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.map)

        mapView = view.findViewById(R.id.map)
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
        locationManager = MapKitFactory.getInstance().createLocationManager()
        marksObject = mapView.map.mapObjects.addCollection()
        mapView.map.addInputListener(this)

        view.findViewById<View>(R.id.userLocation).setOnClickListener {
            try {
                moveCamera(userLocationLayer.cameraPosition()?.target!!)
            } catch (e: Exception) {
                Toast.makeText(context, R.string.retry, Toast.LENGTH_SHORT).show()
            }
        }

        val lat = arguments?.getDouble("lat")
        val lng = arguments?.getDouble("lng")
        open = arguments?.getString("open")

        position = if (open == "newPost" || open == "newEvent") {
            Point(55.75222, 37.61556)
        } else {
            Point(lat!!, lng!!)
        }

        if (lat != null && lng != null) addMarker(Point(lat, lng))

        checkPermission()
        position?.let { moveCamera(it) }

        return view
    }

    private fun moveCamera(point: Point) {
        mapView.map.move(CameraPosition(point, 16F, 0F, 0F))
    }

    private fun addMarker(point: Point) {
        val marker = View(context).apply {
            background = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_place_24)
        }
        mapView.map.mapObjects.addPlacemark(
            point,
            ViewProvider(marker)
        )
    }

    private fun checkPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                mapView.apply {
                    userLocationLayer.isVisible = true
                    userLocationLayer.isHeadingEnabled = false
                }

                val fusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(requireActivity())

                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    println(it)
                }
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onMapTap(map: Map, point: Point) {
        mapView.map.deselectGeoObject()
    }

    override fun onMapLongTap(map: Map, point: Point) {
        mapView.map.mapObjects.clear()
        view?.findViewById<View>(R.id.setGeoLabel)?.visibility = View.VISIBLE
        addMarker(point)

        view?.findViewById<View>(R.id.setGeoLabel)?.setOnClickListener {
            val bundle = Bundle().apply {
                putDouble("lat", point.latitude)
                putDouble("lng", point.longitude)
            }
            when (open) {
                "newPost" -> findNavController().navigate(R.id.newPostFragment, bundle)
                "newEvent" -> findNavController().navigate(R.id.newEventFragment, bundle)
            }
        }
    }

    override fun onLocationUpdated(p0: Location) {}

    override fun onLocationStatusUpdated(p0: LocationStatus) {}
}