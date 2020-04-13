package ca.ulaval.ima.mp.ui.nearList

import androidx.paging.PageKeyedDataSource
import ca.ulaval.ima.mp.ApiHelper
import ca.ulaval.ima.mp.domain.Restaurant
import ca.ulaval.ima.mp.domain.Review
import okhttp3.Response
import org.json.JSONObject

class RestaurantDataSource(latitude: Double, longitude: Double) :
    PageKeyedDataSource<Int, Restaurant>() {
    private val latitude: Double
    private val longitude: Double
    val FIRST_PAGE = 1
    val PAGE_SIZE = 6
    val apiHelper: ApiHelper = ApiHelper()
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Restaurant>
    ) {
        apiHelper.getRestaurantsWithinRadius(
            latitude,
            longitude,
            FIRST_PAGE,
            PAGE_SIZE,
            9999999,
            object :
                ApiHelper.HttpCallback {
                override fun onFailure(response: Response?, throwable: Throwable?) {
                    TODO("Not yet implemented")
                }

                override fun onSuccess(response: Response?) {
                    val restaurantArray = ArrayList<Restaurant>()
                    val jsonResponse = JSONObject(response?.body()!!.string())
                    val content = jsonResponse.getJSONObject("content")
                    val restaurantsJSONArray = content.getJSONArray("results")
                    for (i in 0 until restaurantsJSONArray.length()) {
                        val restaurantJson = restaurantsJSONArray.getJSONObject(i)
                        val restaurant: Restaurant =
                            Restaurant.fromJson(restaurantJson.toString())!!
                        restaurantArray.add(restaurant)
                    }

                    callback.onResult(restaurantArray, null, FIRST_PAGE + 1)
                }

            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Restaurant>) {
        apiHelper.getRestaurantsWithinRadius(
            latitude,
            longitude,
            params.key,
            PAGE_SIZE,
            9999999,
            object :
                ApiHelper.HttpCallback {
                override fun onFailure(response: Response?, throwable: Throwable?) {
                    return
                }

                override fun onSuccess(response: Response?) {
                    val restaurantArray = ArrayList<Restaurant>()
                    val jsonResponse = JSONObject(response?.body()!!.string())
                    val content = jsonResponse.getJSONObject("content")
                    val restaurantsJSONArray = content.getJSONArray("results")
                    for (i in 0 until restaurantsJSONArray.length()) {
                        val restaurantJson = restaurantsJSONArray.getJSONObject(i)
                        val restaurant: Restaurant =
                            Restaurant.fromJson(restaurantJson.toString())!!
                        restaurantArray.add(restaurant)
                    }

                    var key: Int = 0
                    key = (if (content.get("next") != null) {
                        params.key + 1
                    } else {
                        null
                    }) as Int

                    callback.onResult(restaurantArray, key)
                }

            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Restaurant>) {

        apiHelper.getRestaurantsWithinRadius(
            latitude,
            longitude,
            params.key,
            PAGE_SIZE,
            9999999,
            object :
                ApiHelper.HttpCallback {
                override fun onFailure(response: Response?, throwable: Throwable?) {
                    return
                }

                override fun onSuccess(response: Response?) {
                    var key: Int? = 0
                    key = if (params.key > 1) {
                        params.key - 1
                    } else {
                        null
                    }

                    val restaurantArray = ArrayList<Restaurant>()
                    val jsonResponse = JSONObject(response?.body()!!.string())
                    val content = jsonResponse.getJSONObject("content")
                    val restaurantsJSONArray = content.getJSONArray("results")
                    for (i in 0 until restaurantsJSONArray.length()) {
                        val restaurantJson = restaurantsJSONArray.getJSONObject(i)
                        val restaurant: Restaurant =
                            Restaurant.fromJson(restaurantJson.toString())!!
                        restaurantArray.add(restaurant)
                    }


                    callback.onResult(restaurantArray, key)
                }

            })
    }

    init {
        this.latitude = latitude
        this.longitude = longitude
    }

}