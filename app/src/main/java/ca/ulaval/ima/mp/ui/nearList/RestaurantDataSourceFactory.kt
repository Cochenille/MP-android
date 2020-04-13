package ca.ulaval.ima.mp.ui.nearList

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import ca.ulaval.ima.mp.domain.Restaurant

class RestaurantDataSourceFactory : DataSource.Factory<Int, Restaurant>() {
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    val restaurantLiveDataSource: MutableLiveData<RestaurantDataSource> =
        MutableLiveData()
    private var restaurantDataSource: RestaurantDataSource? = null


    override fun create(): DataSource<Int, Restaurant> {
        restaurantDataSource = RestaurantDataSource(latitude,longitude)
        restaurantLiveDataSource.postValue(restaurantDataSource)
        return restaurantDataSource as RestaurantDataSource
    }

    fun setLatLong(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}