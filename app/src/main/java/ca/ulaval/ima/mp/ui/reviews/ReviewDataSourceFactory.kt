package ca. ulaval.ima.mp.ui.reviews

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import ca.ulaval.ima.mp.domain.Review

class ReviewDataSourceFactory : DataSource.Factory<Int, Review>() {
    private var restoId: Long = 0
    val reviewLiveDataSource: MutableLiveData<ReviewDataSource> =
        MutableLiveData()
    private var reviewDataSource: ReviewDataSource? = null

    fun setRestoId(restoId: Long) {
        this.restoId = restoId
    }
    override fun create(): DataSource<Int, Review> {
        reviewDataSource =  ReviewDataSource(restoId)
        reviewLiveDataSource.postValue(reviewDataSource)
        return reviewDataSource as ReviewDataSource
    }

}