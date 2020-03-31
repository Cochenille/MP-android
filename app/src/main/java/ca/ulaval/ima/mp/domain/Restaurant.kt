package ca.ulaval.ima.mp.domain

import com.beust.klaxon.*

private val klaxon = Klaxon()

data class Restaurant (
    val id: Long,
    val name: String,
    val cuisine: List<Cuisine>,

    @Json(name = "review_count")
    val reviewCount: Long,

    @Json(name = "review_average")
    val reviewAverage: Double,

    val image: String,
    val distance: Double,
    val location: Location
) {
    public fun toJson() = klaxon.toJsonString(this)

    companion object {
        public fun fromJson(json: String) = klaxon.parse<Restaurant>(json)
    }
}

data class Cuisine (
    val id: Long,
    val name: String
)

data class Location (
    val latitude: Double,
    val longitude: Double
)