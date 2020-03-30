package ca.ulaval.ima.mp.ui.map
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.MapsInitializer

import com.google.android.gms.maps.MapView
import android.os.Bundle

import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.ApiHelper
import okhttp3.OkHttpClient


class RestoMapFragment : Fragment() {

    internal lateinit var mMapView: MapView
    private var googleMap: GoogleMap? = null
    private var apiHelper:ApiHelper = ApiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(ca.ulaval.ima.mp.R.layout.fragment_map, container, false)


        mMapView = root.findViewById(ca.ulaval.ima.mp.R.id.mapView)
        mMapView.onCreate(savedInstanceState)

        mMapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView.getMapAsync { mMap ->
            googleMap = mMap
            if (ContextCompat.checkSelfPermission(root.context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(root.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                // For showing a move to my location button
                googleMap!!.isMyLocationEnabled = true

                // For dropping a marker at a point on the Map
                val ulaval = LatLng(46.781918, -71.274810)
                googleMap!!.addMarker(MarkerOptions().position(ulaval).title("Marker Title").snippet("Marker Description"))

                // For zooming automatically to the location of the marker
                val cameraPosition = CameraPosition.Builder().target(ulaval).zoom(12f).build()
                googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            } else {
                Toast.makeText(context, "FUCK", Toast.LENGTH_LONG).show()
            }

            getRestaurants()


        }

        return root
    }

    fun getRestaurants(){

    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

}