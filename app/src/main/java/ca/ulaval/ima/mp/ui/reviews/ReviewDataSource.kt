package ca.ulaval.ima.mp.ui.reviews

import androidx.paging.PageKeyedDataSource
import ca.ulaval.ima.mp.ApiHelper
import ca.ulaval.ima.mp.domain.Review
import okhttp3.Response
import org.json.JSONObject

class ReviewDataSource(restaurantId: Long) : PageKeyedDataSource<Int, Review>() {

    private val restoID: Long
    val FIRST_PAGE = 1
    val apiHelper: ApiHelper = ApiHelper()

    init {
        restoID = restaurantId
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Review>
    ) {
        apiHelper.getRestaurantReviews(restoID, FIRST_PAGE, object :
            ApiHelper.HttpCallback {
            override fun onFailure(response: Response?, throwable: Throwable?) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(response: Response?) {
                val reviewArray = ArrayList<Review>()
                val jsonResponse = JSONObject(response?.body()!!.string())
                val jsonContent = jsonResponse.getJSONObject("content")
                val reviewJSONAray = jsonContent.getJSONArray("results")
                for (i in 0 until reviewJSONAray.length()) {
                    val reviewJSONObject = reviewJSONAray.getJSONObject(i)
                    val review: Review = Review.fromJson(reviewJSONObject.toString())!!
                    reviewArray.add(review)
                }

                callback.onResult(reviewArray, null, FIRST_PAGE + 1)
            }

        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Review>) {

        apiHelper.getRestaurantReviews(restoID, params.key, object :
            ApiHelper.HttpCallback {
            override fun onFailure(response: Response?, throwable: Throwable?) {
                return
            }

            override fun onSuccess(response: Response?) {


                val reviewArray = ArrayList<Review>()
                val jsonResponse = JSONObject(response?.body()!!.string())
                val jsonContent = jsonResponse.getJSONObject("content")
                val reviewJSONAray = jsonContent.getJSONArray("results")
                for (i in 0 until reviewJSONAray.length()) {
                    val reviewJSONObject = reviewJSONAray.getJSONObject(i)
                    val review: Review = Review.fromJson(reviewJSONObject.toString())!!
                    reviewArray.add(review)
                }

                var key = 0
                key = (if (jsonContent.get("next") != null) {
                    params.key + 1
                } else {
                    null
                }) as Int

                callback.onResult(reviewArray, key)
            }

        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Review>) {
        apiHelper.getRestaurantReviews(restoID, params.key, object :
            ApiHelper.HttpCallback {
            override fun onFailure(response: Response?, throwable: Throwable?) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(response: Response?) {

                var key: Int? = 0
                key = if (params.key > 1) {
                    params.key - 1
                } else {
                    null
                }

                val reviewArray = ArrayList<Review>()
                val jsonResponse = JSONObject(response?.body()!!.string())
                val jsonContent = jsonResponse.getJSONObject("content")
                val reviewJSONAray = jsonContent.getJSONArray("results")
                for (i in 0 until reviewJSONAray.length()) {
                    val reviewJSONObject = reviewJSONAray.getJSONObject(i)
                    val review: Review = Review.fromJson(reviewJSONObject.toString())!!
                    reviewArray.add(review)
                }

                callback.onResult(reviewArray, key)
            }

        })
    }

}