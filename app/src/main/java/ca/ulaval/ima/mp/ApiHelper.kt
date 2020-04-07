package ca.ulaval.ima.mp

import android.os.Handler
import android.os.Looper
import okhttp3.*
import java.io.IOException

class ApiHelper {
    val client: OkHttpClient = OkHttpClient()
    val JSON = MediaType.parse("application/json; charset=utf-8")
    var identificationToken = ""


    fun getRestaurantsWithinRadius(
        page: Int?,
        latitude: Double?,
        longitude: Double?,
        radius: Int?,
        callback: HttpCallback
    ) {
        var URL = String.format(
            "https://kungry.ca/api/v1/restaurant/search/?page=%d&latitude=%f&longitude=%f&radius=%d",
            page,
            latitude,
            longitude,
            radius
        )
        val request = Request.Builder()
            .url(URL)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val mainHandler = Handler(
                        Looper.getMainLooper()
                    )
                    mainHandler.post {
                        callback.onSuccess(response)
                    }
                } else {
                    callback.onFailure(response, null)
                }
            }
        })
    }

    fun getRestaurantDetails(restaurandId: Long, callback: HttpCallback) {
        var URL = String.format("https://kungry.ca/api/v1/restaurant/%d/", restaurandId)
        val request = Request.Builder()
            .url(URL)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val mainHandler = Handler(
                        Looper.getMainLooper()
                    )
                    mainHandler.post {
                        callback.onSuccess(response)
                    }
                } else {
                    callback.onFailure(response, null)
                }
            }
        })
    }

    interface HttpCallback {
        /**
         * called when the server response was not 2xx or when an exception was thrown in the process
         * @param response - in case of server error (4xx, 5xx) this contains the server response
         * in case of IO exception this is null
         * @param throwable - contains the exception. in case of server error (4xx, 5xx) this is null
         */
        fun onFailure(response: Response?, throwable: Throwable?)

        /**
         * contains the server response
         * @param response
         */
        fun onSuccess(response: Response?)
    }


}