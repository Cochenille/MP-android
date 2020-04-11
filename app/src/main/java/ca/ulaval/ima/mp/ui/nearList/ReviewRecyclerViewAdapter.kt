package ca.ulaval.ima.mp.ui.nearList


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.domain.Review
import com.squareup.picasso.Picasso


class ReviewRecyclerViewAdapter(myDataset: List<Review>) :
    RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder>() {
    private val mDataset: List<Review>
    private val limit = 10
    private var onItemClickListener: OnItemClickListener? = null

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var reviewDateTextView: TextView
        var reviewerNameTextView: TextView
        var reviewCommentTextView: TextView
        var ratingBar: RatingBar
        var imageView:ImageView

        init {
            reviewDateTextView = view.findViewById(R.id.date_textview)
            reviewerNameTextView = view.findViewById(R.id.reviewer_name_textview)
            reviewCommentTextView = view.findViewById(R.id.review_text)
            ratingBar = view.findViewById(R.id.ratingBar)
            imageView = view.findViewById(R.id.review_image)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // create a new view
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_row_item, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val review: Review = mDataset[position]
        holder.reviewDateTextView.text = review.date
        holder.reviewerNameTextView.text =
            String.format("%s %s", review.creator.firstName, review.creator.lastName)
        holder.reviewCommentTextView.text = review.comment
        holder.ratingBar.rating = review.stars.toFloat()
        if(review.image != null){
            Picasso.get().load(review.image).fit().into(holder.imageView)
        }else{
            holder.imageView.visibility = View.GONE
        }
        val listener =
            View.OnClickListener { onItemClickListener!!.onItemClick(review) }
        holder.view.setOnClickListener(listener)
    }

    override fun getItemCount(): Int {
        return mDataset.size;
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(item: Review?)
    }

    init {
        mDataset = myDataset
    }
}