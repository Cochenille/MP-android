package ca.ulaval.ima.mp.ui.nearList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.domain.Restaurant
import com.squareup.picasso.Picasso
import java.util.*


class RestaurantsRecyclerViewAdapter(myDataset: ArrayList<Restaurant>) :
    RecyclerView.Adapter<RestaurantsRecyclerViewAdapter.ViewHolder>() {
    private val mDataset: ArrayList<Restaurant>
    private var onItemClickListener: OnItemClickListener? = null

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var restauranNameTextView: TextView
        var imageView: ImageView
        var distanceTextView: TextView
        var ratingBar: RatingBar
        var ratingCountTextView: TextView

        init {
            restauranNameTextView = view.findViewById(R.id.restaurant_name_textview)
            imageView = view.findViewById(R.id.restaurant_image_view)
            distanceTextView = view.findViewById(R.id.distance_textiview)
            ratingBar = view.findViewById(R.id.stars_layout)
            ratingCountTextView = view.findViewById(R.id.number_of_reviews_textview)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // create a new view
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.resto_row_items, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val restaurant: Restaurant = mDataset[position]
        holder.restauranNameTextView.text = restaurant.name
        Picasso.get().load(restaurant.image).fit().into(holder.imageView)
        holder.distanceTextView.text = String.format("%.1f km",restaurant.distance)
        holder.ratingBar.rating = restaurant.reviewAverage.toFloat()
        holder.ratingCountTextView.text = String.format("(%d)",restaurant.reviewCount)
        val listener =
            View.OnClickListener { onItemClickListener!!.onItemClick(restaurant) }
        holder.view.setOnClickListener(listener)
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(item: Restaurant?)
    }

    init {
        mDataset = myDataset
    }
}