package ca.ulaval.ima.mp.ui.reviews

import ca.ulaval.ima.mp.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.ImageActivity
import ca.ulaval.ima.mp.domain.Review
import com.squareup.picasso.Picasso

class ReviewPagedAdapter (private val context: Context) :
    PagedListAdapter<Review, ReviewPagedAdapter.ViewHolder?>(
        DIFF_CALLBACK
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.review_row_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var reviewDateTextView: TextView = view.findViewById(R.id.date_textview)
        var reviewerNameTextView: TextView = view.findViewById(R.id.reviewer_name_textview)
        var reviewCommentTextView: TextView = view.findViewById(R.id.review_text)
        var ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        var imageView:ImageView = view.findViewById( R.id.review_image)

    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Review> =
            object : DiffUtil.ItemCallback<Review>() {
                override fun areItemsTheSame(
                    oldItem: Review,
                    newItem: Review
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Review,
                    newItem: Review
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review: Review? = getItem(position)
        holder.reviewDateTextView.text = review!!.date
        holder.reviewerNameTextView.text =
            String.format("%s %s", review.creator.firstName, review.creator.lastName)
        holder.reviewCommentTextView.text = review.comment
        holder.ratingBar.rating = review.stars.toFloat()
        holder.imageView.visibility = View.VISIBLE
        if(review.image != null){
            Picasso.get().load(review.image).fit().into(holder.imageView)
            holder.imageView.setOnClickListener {
                val intent = Intent(holder.view.context, ImageActivity::class.java)
                intent.putExtra("image", review.image)
                holder.view.context.startActivity(intent)
            }
        }else{
            holder.imageView.visibility = View.GONE
        }
    }

}