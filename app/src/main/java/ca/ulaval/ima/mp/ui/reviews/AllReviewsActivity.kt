package ca.ulaval.ima.mp.ui.reviews

import android.os.Bundle
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.domain.Review


class AllReviewsActivity : AppCompatActivity() {
    private lateinit var recycledView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_resto_reviews)
        recycledView = findViewById(R.id.all_review_recycler_view)
        val restoId = intent.getLongExtra("restoID", 0)
        layoutManager = LinearLayoutManager(this)
        recycledView.layoutManager = layoutManager
        val reviewViewModel: ReviewViewModel =
            ViewModelProviders.of(this).get(ReviewViewModel::class.java)
        reviewViewModel.setRestoId(restoId)

        val reviewPagedAdapter = ReviewPagedAdapter(this)

        reviewViewModel.reviewPagedList.observe(this,
            Observer<PagedList<Review>> { t -> reviewPagedAdapter.submitList(t) })

        recycledView.adapter = reviewPagedAdapter

        val reviewCount = intent.getLongExtra("reviewCount",0)
        val reviewCountTextView = findViewById<TextView>(R.id.review_count_textview)
        reviewCountTextView.text = String.format("(%d)",reviewCount)

    }

}