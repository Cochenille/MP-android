package ca.ulaval.ima.mp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.domain.RestaurantDetails
import ca.ulaval.ima.mp.ui.nearList.RestaurantsRecyclerViewAdapter
import ca.ulaval.ima.mp.ui.nearList.ReviewRecyclerViewAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class RestoDetailsActivity : AppCompatActivity() {

    val requestcode: Int = 0
    private var apiHelper: ApiHelper = ApiHelper()
    private var restaurantDetails: RestaurantDetails? = null
    private var googleMap: GoogleMap? = null
    private var currentPosition: LatLng? = null
    var identificationToken: String? = ""
    private lateinit var recycledView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: ReviewRecyclerViewAdapter
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_details_activity)
        val restaurantId = intent.getLongExtra("restaurantId", 0)
        val token = intent.getStringExtra("token")
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        currentPosition = LatLng(latitude, longitude)

        identificationToken = token
        recycledView = findViewById(R.id.review_recyclerview)
        layoutManager = LinearLayoutManager(this)
        recycledView.layoutManager = layoutManager

        getRestaurantDetails(restaurantId)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        //affiche le bon bouton en fonction de si l'usager est connecté ou non
        val buttonBasDePage = findViewById<Button>(R.id.buttonConnexion)
        val textViewLaisserEval = findViewById<TextView>(R.id.textViewConnexionLabel)
        textViewLaisserEval.visibility = View.VISIBLE

        if (identificationToken != null && identificationToken != "") {
            buttonBasDePage.text = "Laisser une évaluation"
            buttonBasDePage.setBackgroundResource(R.drawable.custom_rounded_button_black)
            buttonBasDePage.setOnClickListener {

                val intent = Intent(this, NewEvalActivity::class.java)
                intent.putExtra("token", identificationToken)
                intent.putExtra("restoId", restaurantId)
                startActivity(intent)
            }
            textViewLaisserEval.visibility = View.INVISIBLE
        } else {
            buttonBasDePage.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("gotoConnexion", "true")
                startActivityForResult(intent, requestcode)
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == this.requestcode) {
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, NewEvalActivity::class.java)
                intent.putExtra("token", identificationToken)
                startActivity(intent)
            }
        }
    }

    private fun setViewContent() {
        val nameTextView = findViewById<TextView>(R.id.restaurant_name_textview)
        val imageView = findViewById<ImageView>(R.id.restaurant_image_view)
        val distanceTextView = findViewById<TextView>(R.id.distance_textiview)
        val numberOfReviewTextView = findViewById<TextView>(R.id.number_of_reviews_textview)
        val ratingBar = findViewById<RatingBar>(R.id.stars_layout)
        val phoneButton = findViewById<Button>(R.id.phone_button)
        val webSiteButton = findViewById<Button>(R.id.web_site_button)
        val typeCuisineTextView = findViewById<TextView>(R.id.type_cuisine_textview)
        val reviewCountTextView = findViewById<TextView>(R.id.review_count_textview)


        Picasso.get().load(restaurantDetails?.image).fit().into(imageView)
        nameTextView.text = restaurantDetails?.name
        typeCuisineTextView.text = String.format(
            "%s • %s", restaurantDetails?.type,
            restaurantDetails?.cuisine?.get(0)?.name
        )
        distanceTextView.text = String.format("%.2f km", restaurantDetails?.distance)
        numberOfReviewTextView.text = String.format("(%d)", restaurantDetails?.reviewCount)
        ratingBar.rating = restaurantDetails?.reviewAverage!!.toFloat()
        phoneButton.text = restaurantDetails?.phoneNumber
        phoneButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(String.format("tel:%s", restaurantDetails!!.phoneNumber))
            startActivity(intent)
        }
        webSiteButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(restaurantDetails!!.website))
            startActivity(browserIntent)
        }
        reviewCountTextView.text = String.format("(%d)", restaurantDetails!!.reviewCount)
        setOpeningHours()


    }

    private fun setOpeningHours() {
        var textViewToFill: TextView? = null
        for (openingHour in restaurantDetails!!.openingHours) {
            when (openingHour.day) {
                "MON" -> textViewToFill = findViewById(R.id.monday_hours)
                "TUE" -> textViewToFill = findViewById(R.id.tuesday_hours)
                "WED" -> textViewToFill = findViewById(R.id.wednesday_hours)
                "THU" -> textViewToFill = findViewById(R.id.thursday_hours)
                "FRI" -> textViewToFill = findViewById(R.id.friday_hours)
                "SAT" -> textViewToFill = findViewById(R.id.saturday_hours)
                "SUN" -> textViewToFill = findViewById(R.id.sunday_hours)

            }
            if (openingHour.openingHour == null || openingHour.closingHour == null) {
                textViewToFill!!.text = "Fermé"
            } else {
                textViewToFill!!.text =
                    String.format("%s à %s", openingHour.openingHour, openingHour.closingHour)

            }
        }
    }

    private fun setMapView() {
        mapView.getMapAsync { mMap ->
            googleMap = mMap
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // For showing a move to my location button
                googleMap!!.isMyLocationEnabled = true

                // For dropping a marker at a point on the Map
                val markerPosition = LatLng(
                    restaurantDetails!!.location.latitude,
                    restaurantDetails!!.location.longitude
                )
                googleMap!!.addMarker(
                    MarkerOptions().position(markerPosition).title("Marker Title")
                        .snippet("Marker Description")
                        .icon(bitmapDescriptorFromVector(this, R.drawable.ic_black_map_marker))
                )

                // For zooming automatically to the location of the marker
                val cameraPosition =
                    CameraPosition.Builder().target(markerPosition).zoom(12f).build()
                googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            } else {
                Toast.makeText(this, "Didnt Work", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getRestaurantDetails(restaurantId: Long) {
        apiHelper.getRestaurantDetails(restaurantId,
            currentPosition!!.latitude,
            currentPosition!!.longitude,
            object : ApiHelper.HttpCallback {
                override fun onFailure(response: Response?, throwable: Throwable?) {
                }

                override fun onSuccess(response: Response?) {
                    try {
                        val jsonResponse = JSONObject(response?.body()!!.string())
                        val jsonRestaurantDetails = jsonResponse.getJSONObject("content")
                        restaurantDetails =
                            RestaurantDetails.fromJson(jsonRestaurantDetails.toString())!!
                        setViewContent()
                        setMapView()
                        setRecycledView()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun setRecycledView() {
        adapter = ReviewRecyclerViewAdapter(restaurantDetails!!.reviews)
        recycledView.adapter = adapter
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
}