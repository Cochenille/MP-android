package ca.ulaval.ima.mp.ui.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ca.ulaval.ima.mp.domain.Review

class ReviewViewModel : ViewModel() {
    var reviewPagedList: LiveData<PagedList<Review>>
    private var liveDataSource: LiveData<ReviewDataSource>
    private var reviewDataSourceFactory:ReviewDataSourceFactory = ReviewDataSourceFactory()

    init {
        liveDataSource = reviewDataSourceFactory.reviewLiveDataSource

        val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(5).build()

        reviewPagedList = LivePagedListBuilder(reviewDataSourceFactory, config).build()
    }

    fun setRestoId(restoId:Long){
        reviewDataSourceFactory.setRestoId(restoId)
    }
}