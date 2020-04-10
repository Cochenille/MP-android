package ca.ulaval.ima.mp

import android.os.Handler
import android.os.Looper
import okhttp3.*
import java.io.IOException
import java.util.*

class ApiHelper {
    val client: OkHttpClient = OkHttpClient()
    val JSON = MediaType.parse("application/json; charset=utf-8")


    fun getRestaurantsWithinRadius(
        latitude: Double?,
        longitude: Double?,
        radius: Int?,
        callback: HttpCallback
    ) {
        var URL = String.format(
            Locale.US,
            "https://kungry.ca/api/v1/restaurant/search/?latitude=%f&longitude=%f&radius=%d",
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

    fun getRestaurantDetails(
        restaurandId: Long,
        latitude: Double,
        longitude: Double,
        callback: HttpCallback
    ) {
        var URL = String.format(
            Locale.US,
            "https://kungry.ca/api/v1/restaurant/%d/?latitude=%f&longitude=%f",
            restaurandId,
            latitude,
            longitude
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

    fun login(
        username: String?,
        password: String?,
        clientId: String?,
        clientSecret: String?,
        callback: HttpCallback
    ) {

        val formBody: RequestBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("email", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("https://kungry.ca/api/v1/account/login/")
            .post(formBody)
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

    fun createUser(
        nom: String?,
        prenom: String?,
        username: String?,
        password: String?,
        clientId: String?,
        clientSecret: String?,
        callback: HttpCallback
    ) {

        val formBody: RequestBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("first_name", prenom)
            .add("last_name", nom)
            .add("email", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("https://kungry.ca/api/v1/account/")
            .post(formBody)
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

    fun getUserInfo(token: String?, callback: HttpCallback) {

        val request = Request.Builder()
            .url("https://kungry.ca/api/v1/account/me/")
            .header("Authorization", "Bearer " + token)
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

    fun getUserReview(pageNo: Int, reviewByPage: Int, token: String?, callback: HttpCallback) {
        var URL = String.format(
            "https://kungry.ca/api/v1/review/mine/?page=%d&page_size=%d",
            pageNo,
            reviewByPage
        )
        val request = Request.Builder()
            .url(URL)
            .header("Authorization", "Bearer " + token)
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

    fun submitReview(
        restoId: Int?,
        note: Int?,
        commentaire: String,
        token: String?,
        callback: HttpCallback
    ) {

        val formBody: RequestBody = FormBody.Builder()
            .add("restaurant_id", restoId.toString())
            .add("stars", note.toString())
            .add("comments", commentaire)
            .build()
        val request = Request.Builder()
            .url("https://kungry.ca/api/v1/review/")
            .header("Authorization", "Bearer " + token)
            .post(formBody)
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