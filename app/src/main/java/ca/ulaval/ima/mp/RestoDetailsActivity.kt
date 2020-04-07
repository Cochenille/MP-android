package ca.ulaval.ima.mp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.ulaval.ima.mp.domain.RestaurantDetails
import com.squareup.picasso.Picasso
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

class RestoDetailsActivity : AppCompatActivity () {

    private var apiHelper: ApiHelper = ApiHelper()
    private  var restaurantDetails: RestaurantDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_details_activity)
        val restaurantId = intent.getLongExtra("restaurantId",0)
        getRestaunrantDetails(restaurantId)

    }

    private fun setViewContent() {
        var nameTextView = findViewById<TextView>(R.id.restaurant_name_textview)
        var imageView = findViewById<ImageView>(R.id.restaurant_image_view)
        var distanceTextView = findViewById<TextView>(R.id.distance_textiview)
        var numberOfReviewTextView = findViewById<TextView>(R.id.number_of_reviews_textview)
        var ratingBar = findViewById<RatingBar>(R.id.stars_layout)
        var phoneButton = findViewById<Button>(R.id.phone_button)
        var websiteButton = findViewById<Button>(R.id.web_site_button)
        Picasso.get().load(restaurantDetails?.image).into(imageView)
        nameTextView.text = restaurantDetails?.name
        distanceTextView.text = String.format("%.2f km",restaurantDetails?.distance)
        numberOfReviewTextView.text = String.format("(%d)",restaurantDetails?.reviewCount)
        ratingBar.rating = restaurantDetails?.reviewAverage!!.toFloat()
        phoneButton.text = restaurantDetails?.phoneNumber
        websiteButton.text = restaurantDetails?.website

    }


    private fun getRestaunrantDetails(restaurantId: Long) {
        apiHelper.getRestaurantDetails(restaurantId,
            object : ApiHelper.HttpCallback{
            override fun onFailure(response: Response?, throwable: Throwable?) {
            }

            override fun onSuccess(response: Response?) {
                try {
                    val jsonResponse = JSONObject(response?.body()!!.string())
                    val jsonRestaurantDetails = jsonResponse.getJSONObject("content")
                    restaurantDetails = RestaurantDetails.fromJson(jsonRestaurantDetails.toString())!!
                    setViewContent()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }
}