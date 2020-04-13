package ca.ulaval.ima.mp.ui.nearList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.domain.Restaurant
import com.squareup.picasso.Picasso


class RestaurantsPageAdapter(private val context: Context) :
    PagedListAdapter<Restaurant, RestaurantsPageAdapter.ViewHolder>(DIFF_CALLBACK) {
    private var onItemClickListener: OnItemClickListener? = null

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var restauranNameTextView: TextView = view.findViewById(R.id.restaurant_name_textview)
        var imageView: ImageView = view.findViewById(R.id.restaurant_image_view)
        var distanceTextView: TextView = view.findViewById(R.id.distance_textiview)
        var ratingBar: RatingBar = view.findViewById(R.id.stars_layout)
        var ratingCountTextView: TextView = view.findViewById(R.id.number_of_reviews_textview)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // create a new view
        val v: View = LayoutInflater.from(context)
            .inflate(R.layout.resto_row_items, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val restaurant: Restaurant? = getItem(position)
        holder.restauranNameTextView.text = restaurant!!.name
        Picasso.get().load(restaurant.image).fit().into(holder.imageView)
        holder.distanceTextView.text = String.format("%.1f km", restaurant.distance)
        holder.ratingBar.rating = restaurant.reviewAverage.toFloat()
        holder.ratingCountTextView.text = String.format("(%d)", restaurant.reviewCount)
        val listener =
            View.OnClickListener { onItemClickListener!!.onItemClick(restaurant) }
        holder.view.setOnClickListener(listener)
    }


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(item: Restaurant?)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Restaurant> =
            object : DiffUtil.ItemCallback<Restaurant>() {
                override fun areItemsTheSame(
                    oldItem: Restaurant,
                    newItem: Restaurant
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Restaurant,
                    newItem: Restaurant
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

}