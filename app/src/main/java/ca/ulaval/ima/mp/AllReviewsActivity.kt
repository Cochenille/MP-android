package ca.ulaval.ima.mp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.domain.Review
import ca.ulaval.ima.mp.ui.nearList.ReviewRecyclerViewAdapter
import okhttp3.Response
import org.json.JSONObject

class AllReviewsActivity : AppCompatActivity() {
    private lateinit var recycledView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: ReviewRecyclerViewAdapter
    private val reviewArray = ArrayList<Review>()
    private val apiHelper: ApiHelper = ApiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_resto_reviews)
        recycledView = findViewById(R.id.all_review_recycler_view)
        val restoId = intent.getLongExtra("restoID", 0)
        layoutManager = LinearLayoutManager(this)
        recycledView.layoutManager = layoutManager
        getReviews(restoId)
    }

    private fun getReviews(restoId: Long) {
        apiHelper.getRestaurantReviews(restoId, object : ApiHelper.HttpCallback {
            override fun onFailure(response: Response?, throwable: Throwable?) {
            }

            override fun onSuccess(response: Response?) {
                val jsonResponse = JSONObject(response?.body()!!.string())
                val jsonContent = jsonResponse.getJSONObject("content")
                val reviewJSONAray = jsonContent.getJSONArray("results")
                for (i in 0 until reviewJSONAray.length()) {
                    val reviewJSONObject = reviewJSONAray.getJSONObject(i)
                    val review: Review = Review.fromJson(reviewJSONObject.toString())!!
                    reviewArray.add(review)
                }
                adapter = ReviewRecyclerViewAdapter(reviewArray)
                recycledView.adapter = adapter
            }

        })
    }
}