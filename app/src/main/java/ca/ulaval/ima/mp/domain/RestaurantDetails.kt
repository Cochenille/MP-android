package ca.ulaval.ima.mp.domain

// To parse the JSON, install Klaxon and do:
//
//   val restaurant = Restaurant.fromJson(jsonString)

import com.beust.klaxon.*

private val klaxon = Klaxon()

data class RestaurantDetails (
    val id: Long,
    val cuisine: List<Cuisine>,
    val distance: Double,

    @Json(name = "review_count")
    val reviewCount: Long,

    @Json(name = "opening_hours")
    val openingHours: List<OpeningHour>,

    @Json(name = "review_average")
    val reviewAverage: Double,

    val location: Location,
    val reviews: List<Review>,
    val name: String,
    val website: String,

    @Json(name = "phone_number")
    val phoneNumber: String,

    val image: String,
    val type: String
) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<RestaurantDetails>(json)
    }
}

data class OpeningHour (
    val id: Long,

    @Json(name = "opening_hour")
    val openingHour: String,

    @Json(name = "closing_hour")
    val closingHour: String,

    val day: String
)

data class Review (
    val id: Long,
    val creator: Creator,
    val stars: Long,
    val image: String? = null,
    val comment: String? = null,
    val date: String
)

data class Creator (
    @Json(name = "first_name")
    val firstName: String,

    @Json(name = "last_name")
    val lastName: String
)
