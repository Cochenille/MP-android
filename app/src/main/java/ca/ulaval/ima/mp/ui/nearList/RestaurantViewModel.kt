package ca.ulaval.ima.mp.ui.nearList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ca.ulaval.ima.mp.domain.Restaurant

class RestaurantViewModel: ViewModel(){
    var restaurantPagedList: LiveData<PagedList<Restaurant>>
    private var liveDataSource: LiveData<RestaurantDataSource>
    private var restaurantDataSourceFactory: RestaurantDataSourceFactory = RestaurantDataSourceFactory()

    init {
        liveDataSource = restaurantDataSourceFactory.restaurantLiveDataSource

        val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(6).build()

        restaurantPagedList = LivePagedListBuilder(restaurantDataSourceFactory, config).build()
    }

    fun setLatLong(lat: Double,long: Double){
        restaurantDataSourceFactory.setLatLong(lat,long)
    }
}