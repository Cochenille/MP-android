package ca.ulaval.ima.mp

import android.nfc.tech.MifareUltralight.PAGE_SIZE
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
    private var currentPage: Int = 1
    private var nextPage: Int? = null
    private var previousPage: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_resto_reviews)
        recycledView = findViewById(R.id.all_review_recycler_view)
        val restoId = intent.getLongExtra("restoID", 0)
        layoutManager = LinearLayoutManager(this)
        recycledView.layoutManager = layoutManager
        recycledView.addOnScrollListener(recyclerViewOnScrollListener)
        getReviews(restoId)
    }

    private fun getReviews(restoId: Long) {
        apiHelper.getRestaurantReviews(restoId, currentPage, object : ApiHelper.HttpCallback {
            override fun onFailure(response: Response?, throwable: Throwable?) {
            }

            override fun onSuccess(response: Response?) {
                val jsonResponse = JSONObject(response?.body()!!.string())
                val jsonContent = jsonResponse.getJSONObject("content")
                nextPage = jsonContent.getInt("next")
                previousPage = jsonContent.getInt("previous")
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

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

            }
        }
}