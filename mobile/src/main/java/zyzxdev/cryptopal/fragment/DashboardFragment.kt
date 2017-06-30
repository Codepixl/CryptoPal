package zyzxdev.cryptopal.fragment

import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.dashboard.*
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.AddWalletActivity
import zyzxdev.cryptopal.util.DownloadTask
import zyzxdev.cryptopal.util.TaskCompletedCallback


class DashboardFragment : android.support.v4.app.Fragment() {

	private var itemsToRefresh = 0

	override fun onActivityCreated(savedInstanceState: android.os.Bundle?) {
		super.onActivityCreated(savedInstanceState)

		mainListView.adapter = zyzxdev.cryptopal.wallet.Wallet.WalletAdapter(context)

		fab.setOnClickListener { _ ->
			startActivity(android.content.Intent(context, AddWalletActivity::class.java))
		}

		swipeRefresh.setOnRefreshListener {
			refreshData(true)
		}

		if(activity is zyzxdev.cryptopal.activity.MainTabbedActivity)
			(activity as zyzxdev.cryptopal.activity.MainTabbedActivity).refreshIfNecessary()
	}

	override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
		//(activity as AppCompatActivity).setSupportActionBar(toolbar)
		setHasOptionsMenu(true)
		return inflater?.inflate(R.layout.dashboard, container, false)
	}

	override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: android.view.MenuInflater) {
		inflater.inflate(R.menu.menu_dashboard, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return when (item.itemId) {
			R.id.refresh -> {
				refreshData()
				return true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	fun refreshData(override: Boolean = false){
		if(swipeRefresh!!.isRefreshing && !override) return
		swipeRefresh?.isRefreshing = true
		itemsToRefresh = zyzxdev.cryptopal.wallet.WalletHandler.Companion.wallets.size+1
		context.getSharedPreferences("data", android.content.Context.MODE_PRIVATE).edit().putFloat("btcValue", -1f).apply()
		DownloadTask(context).setCallback(object: TaskCompletedCallback {
			override fun taskCompleted(data: Object) {
				refreshedItem()
				try {
					val btc = (data as String).toFloat()
					context.getSharedPreferences("data", android.content.Context.MODE_PRIVATE).edit().putFloat("btcValue", btc).apply()
				}catch(e: NumberFormatException){
					android.widget.Toast.makeText(context, R.string.error_updating, android.widget.Toast.LENGTH_SHORT).show()
				}
			}
		}).execute("https://blockchain.info/q/24hrprice")

		for(wallet in zyzxdev.cryptopal.wallet.WalletHandler.Companion.wallets){
			wallet.refreshBalance(context, object: TaskCompletedCallback {
				override fun taskCompleted(data: Object) {
					refreshedItem()
				}
			})
		}
	}

	private fun refreshedItem(){
		if(mainListView == null) return //We exited the fragment before this loaded
		itemsToRefresh--
		if(itemsToRefresh == 0){
			swipeRefresh?.isRefreshing = false
			(mainListView.adapter as zyzxdev.cryptopal.wallet.Wallet.WalletAdapter).notifyDataSetChanged()
		}
	}

	override fun onResume() {
		super.onResume()
		(mainListView.adapter as zyzxdev.cryptopal.wallet.Wallet.WalletAdapter).notifyDataSetChanged()
	}


}
