package ca.ulaval.ima.mp.ui.map

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ca.ulaval.ima.mp.ApiHelper
import ca.ulaval.ima.mp.MainActivity
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.RestoDetailsActivity
import ca.ulaval.ima.mp.domain.Restaurant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject


class RestoMapFragment : Fragment(), GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowCloseListener {

    private lateinit var mMapView: MapView
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var restoInfoOverlay: ConstraintLayout

    private var acc: MainActivity? = null

    private var googleMap: GoogleMap? = null
    private var apiHelper: ApiHelper = ApiHelper()
    private var restaurantArray = ArrayList<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        mMapView = root.findViewById(R.id.mapView)
        viewFlipper = root.findViewById(R.id.map_overlay)
        restoInfoOverlay = root.findViewById(R.id.restaurant_info_overlay)

        mMapView.onCreate(savedInstanceState)

        mMapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMapView.getMapAsync { mMap ->
            googleMap = mMap
            if (ContextCompat.checkSelfPermission(
                    root.context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    root.context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {

                acc = activity as MainActivity?


                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                criteria.isAltitudeRequired = false
                criteria.isBearingRequired = false
                criteria.isCostAllowed = true
                criteria.powerRequirement = Criteria.POWER_LOW
                val lm: LocationManager =
                    root.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val provider: String = lm.getBestProvider(criteria,true)
                val location: Location? = lm.getLastKnownLocation(provider)


                // For showing a move to my location button
                googleMap!!.isMyLocationEnabled = true
                if(location == null){
                    acc!!.currentPosition= LatLng(46.781893,-71.274699)
                }else{
                    val longitude: Double = location.longitude
                    val latitude: Double = location.latitude
                    acc!!.currentPosition = LatLng(latitude, longitude)
                }
                // For dropping a marker at a point on the Map



                // For zooming automatically to the location of the marker
                val cameraPosition =
                    CameraPosition.Builder().target(acc!!.currentPosition).zoom(12f).build()
                googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            } else {
                Toast.makeText(context, "FUCK", Toast.LENGTH_LONG).show()
            }
            googleMap!!.setOnMarkerClickListener(this)
            googleMap!!.setOnInfoWindowCloseListener(this)
            getRestaurants()


        }

        return root
    }

    private fun getRestaurants() {
        apiHelper.getAllRestaurantsWithinRadius(
            acc!!.currentPosition.latitude,
            acc!!.currentPosition.longitude,
            30,
            object : ApiHelper.HttpCallback {
                override fun onFailure(
                    response: Response?,
                    throwable: Throwable?
                ) {
                }

                override fun onSuccess(response: Response?) {
                    restaurantArray.clear()
                    try {
                        val jsonResponse = JSONObject(response?.body()!!.string())
                        val restaurantsJSONArray = jsonResponse.getJSONArray("content")
                        for (i in 0 until restaurantsJSONArray.length()) {
                            val restaurantJson = restaurantsJSONArray.getJSONObject(i)
                            val restaurant: Restaurant =
                                Restaurant.fromJson(restaurantJson.toString())!!
                            restaurantArray.add(restaurant)
                        }
                        addRestaurantsMarker()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun addRestaurantsMarker() {
        googleMap!!.clear()
        for (restaurant in restaurantArray) {
            var marker: Marker
            var restaurantPos = LatLng(restaurant.location.latitude, restaurant.location.longitude)
            marker = googleMap!!.addMarker(
                MarkerOptions().position(restaurantPos)
                    .icon(bitmapDescriptorFromVector(context!!, R.drawable.ic_pin_1))
                    .title(restaurant.name)
            )
            marker.tag = restaurant
            if(arguments != null){
                var currentRestoId = arguments?.getLong("restoId")
                if(restaurant.id == currentRestoId){
                    onMarkerClick(marker)
                }
            }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
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
        if(::mMapView.isInitialized) {
            mMapView.onDestroy()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker!!.showInfoWindow()
        marker.setIcon(bitmapDescriptorFromVector(context!!, R.drawable.ic_black_map_marker))
        var restaurant: Restaurant = marker.tag as Restaurant
        viewFlipper.displayedChild = 1
        var nameTextView = restoInfoOverlay.findViewById<TextView>(R.id.restaurant_name_textview)
        var imageView = restoInfoOverlay.findViewById<ImageView>(R.id.restaurant_image_view)
        var distanceTextView = restoInfoOverlay.findViewById<TextView>(R.id.distance_textiview)
        var numberOfReviewTextView =
            restoInfoOverlay.findViewById<TextView>(R.id.number_of_reviews_textview)
        var ratingBar = restoInfoOverlay.findViewById<RatingBar>(R.id.stars_layout)
        var typeCuisineTextView = restoInfoOverlay.findViewById<TextView>(R.id.type_cusine_textview)

        Picasso.get().load(restaurant.image).fit().into(imageView)
        nameTextView.text = restaurant.name
        typeCuisineTextView.text = String.format("%s • %s",restaurant.type,restaurant.cuisine[0].name)
        distanceTextView.text = String.format("%.2f km", restaurant.distance)
        numberOfReviewTextView.text = String.format("(%d)", restaurant.reviewCount)
        ratingBar.rating = restaurant.reviewAverage.toFloat()
        restoInfoOverlay.setOnClickListener(View.OnClickListener {
            launchRestoDetailsActivity(restaurant.id)
        })
        return false
    }

    private fun launchRestoDetailsActivity(id: Long) {
        val activity = context as MainActivity
        val intent = Intent(context, RestoDetailsActivity::class.java)
        intent.putExtra("restaurantId", id)
        intent.putExtra("token", activity.identificationToken)
        intent.putExtra("latitude", acc!!.currentPosition.latitude)
        intent.putExtra("longitude", acc!!.currentPosition.longitude)
        startActivity(intent)
    }

    override fun onInfoWindowClose(marker: Marker?) {
        marker!!.setIcon(bitmapDescriptorFromVector(context!!, R.drawable.ic_pin_1))
        viewFlipper.displayedChild = 0
    }

}