package zyzxdev.cryptopal.fragment.dashboard

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_dashboard.*

import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.MainTabbedActivity
import zyzxdev.cryptopal.fragment.dashboard.card.BTCValueCard
import zyzxdev.cryptopal.fragment.dashboard.card.CardManager
import zyzxdev.cryptopal.util.MultiViewAdapter
import zyzxdev.cryptopal.util.DownloadTask
import zyzxdev.cryptopal.util.SwipeDismissListViewTouchListener
import zyzxdev.cryptopal.util.TaskCompletedCallback

class DashboardFragment : Fragment() {
	var isRefreshing = false
	var adapter: CardManager.CardViewAdapter? = null

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		adapter = CardManager.CardViewAdapter(context, CardManager.cards, mainListView)
		mainListView.adapter = adapter

		//Refresh data now, if necessary
		if(activity is MainTabbedActivity)
			if((activity as MainTabbedActivity).shouldRefresh())
				refreshData()
	}

	override fun onResume() {
		super.onResume()
		(mainListView.adapter as CardManager.CardViewAdapter).notifyDataSetChanged()
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		setHasOptionsMenu(true)
		return inflater!!.inflate(R.layout.fragment_dashboard, container, false)
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		inflater?.inflate(R.menu.menu_dashboard, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when(item?.itemId){
			R.id.clearAll -> {
				adapter?.dismissAll()
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun refreshData(override: Boolean = false){
		//If we're already refreshing, and !override, return
		if(isRefreshing && !override) return

		//Set refreshing state to true
		isRefreshing = true

		//Set btcValue to -1 so other activities know that we're refreshing it
		context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putFloat("btcValue", -1f).apply()

		//Download the current 24-hour BTC value, and set it
		DownloadTask(context).setCallback(object: TaskCompletedCallback {
			override fun taskCompleted(data: Any?) {
				isRefreshing = false
				if(mainListView != null)
					(mainListView?.adapter as MultiViewAdapter).notifyDataSetChanged()
				try {
					if(data != null) {
						val btc = (data as String).toFloat()
						context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putFloat("btcValue", btc).apply()
					}
				}catch(e: NumberFormatException){
					Toast.makeText(context, R.string.error_updating, Toast.LENGTH_SHORT).show()
				}
			}
		}).execute("https://blockchain.info/q/24hrprice")
	}
}
