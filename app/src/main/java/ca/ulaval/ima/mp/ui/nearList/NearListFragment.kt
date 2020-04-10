package ca.ulaval.ima.mp.ui.nearList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.ApiHelper
import ca.ulaval.ima.mp.MainActivity
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.RestoDetailsActivity
import ca.ulaval.ima.mp.domain.Restaurant
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject


class NearListFragment : Fragment() {
    private lateinit var recycledView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RestaurantsRecyclerViewAdapter
    private var acc: MainActivity? = null
    private var apiHelper: ApiHelper = ApiHelper()
    private var restaurantArray = ArrayList<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_nearlist, container, false)
        recycledView = root.findViewById(R.id.restaurants_recycleView)

        recycledView.isNestedScrollingEnabled = true

        layoutManager = LinearLayoutManager(this.context)
        recycledView.layoutManager = layoutManager
        acc = activity as MainActivity?
        getRestaurants()
        return root
    }

    private fun getRestaurants() {
        apiHelper.getRestaurantsWithinRadius(
            acc!!.currentPosition.latitude,
            acc!!.currentPosition.longitude,
            10000,
            object : ApiHelper.HttpCallback {
                override fun onFailure(
                    response: Response?,
                    throwable: Throwable?
                ) {
                }

                override fun onSuccess(response: Response?) {
                    restaurantArray.clear()
                    try {
                        val jsonResponse = JSONObject(response?.body()!!.string())
                        val content = jsonResponse.getJSONObject("content")
                        val restaurantsJSONArray = content.getJSONArray("results")
                        for (i in 0 until restaurantsJSONArray.length()) {
                            val restaurantJson = restaurantsJSONArray.getJSONObject(i)
                            val restaurant: Restaurant =
                                Restaurant.fromJson(restaurantJson.toString())!!
                            restaurantArray.add(restaurant)
                        }
                        adapter = RestaurantsRecyclerViewAdapter(restaurantArray)
                        recycledView.adapter = adapter
                        val horizontalDecoration = DividerItemDecoration(
                            recycledView.getContext(),
                            DividerItemDecoration.VERTICAL
                        )
                        val horizontalDivider =
                            ContextCompat.getDrawable(activity!!, R.drawable.horizontal_divider)
                        horizontalDecoration.setDrawable(horizontalDivider!!)
                        recycledView.addItemDecoration(horizontalDecoration)
                        adapter.setOnItemClickListener(object :
                            RestaurantsRecyclerViewAdapter.OnItemClickListener {
                            override fun onItemClick(restaurant: Restaurant?) {
                                launchRestoDetails(restaurant!!.id)
                            }

                        })
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun launchRestoDetails(restaurantId: Long) {
        val activity = context as MainActivity
        val intent = Intent(context, RestoDetailsActivity::class.java)
        intent.putExtra("restaurantId", restaurantId)
        intent.putExtra("token", activity.identificationToken)
        intent.putExtra("latitude", acc!!.currentPosition.latitude)
        intent.putExtra("longitude", acc!!.currentPosition.longitude)
        startActivity(intent)
    }
}