package ca.ulaval.ima.mp.ui.nearList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.ulaval.ima.mp.ApiHelper
import ca.ulaval.ima.mp.MainActivity
import ca.ulaval.ima.mp.R
import ca.ulaval.ima.mp.RestoDetailsActivity
import ca.ulaval.ima.mp.domain.Restaurant


class NearListFragment : Fragment() {
    private lateinit var recycledView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var acc: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_nearlist, container, false)
        recycledView = root.findViewById(R.id.restaurants_recycleView)

        layoutManager = LinearLayoutManager(this.context)
        recycledView.layoutManager = layoutManager
        acc = activity as MainActivity?
        val restaurantViewModel: RestaurantViewModel =
            ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        restaurantViewModel.setLatLong(
            acc!!.currentPosition.latitude,
            acc!!.currentPosition.longitude
        )
        val adapter = RestaurantsPageAdapter(context!!)
        restaurantViewModel.restaurantPagedList.observe(this, Observer<PagedList<Restaurant>> { t ->
            adapter.submitList(t)
        })
        recycledView.adapter = adapter
        val horizontalDecoration = DividerItemDecoration(
            recycledView.context,
            DividerItemDecoration.VERTICAL
        )
        val horizontalDivider =
            ContextCompat.getDrawable(activity!!, R.drawable.horizontal_divider)
        horizontalDecoration.setDrawable(horizontalDivider!!)
        recycledView.addItemDecoration(horizontalDecoration)
        adapter.setOnItemClickListener(object :
            RestaurantsPageAdapter.OnItemClickListener {
            override fun onItemClick(restaurant: Restaurant?) {
                launchRestoDetails(restaurant!!.id)
            }

        })
        return root
    }

    private fun launchRestoDetails(restaurantId: Long) {
        val activity = context as MainActivity
        val intent = Intent(context, RestoDetailsActivity::class.java)
        intent.putExtra("restaurantId", restaurantId)
        intent.putExtra("token", activity.identificationToken)
        intent.putExtra("latitude", acc!!.currentPosition.latitude)
        intent.putExtra("longitude", acc!!.currentPosition.longitude)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == 3) {
                var restoId = data?.getLongExtra("restoID", 0)
                var bundle = Bundle()
                if (restoId != null) {
                    bundle.putLong("restoId", restoId)
                }
                if (data?.getStringExtra("token") != null) {
                    acc?.identificationToken = data.getStringExtra("token")!!
                }
                findNavController().navigate(R.id.navigation_map, bundle)
            }
        }

    }
}