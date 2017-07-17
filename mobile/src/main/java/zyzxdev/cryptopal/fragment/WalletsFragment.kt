package zyzxdev.cryptopal.fragment

import kotlinx.android.synthetic.main.fragment_wallets.*
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.activity.AddWalletActivity
import zyzxdev.cryptopal.activity.MainTabbedActivity
import zyzxdev.cryptopal.util.TaskCompletedCallback
import zyzxdev.cryptopal.wallet.Wallet


class WalletsFragment : android.support.v4.app.Fragment() {

	private var itemsToRefresh = 0

	override fun onActivityCreated(savedInstanceState: android.os.Bundle?) {
		super.onActivityCreated(savedInstanceState)

		//Set ListView Adapter
		mainListView.adapter = Wallet.WalletAdapter(context)

		//Start AddWalletActivity on FAB press
		fab.setOnClickListener {
			startActivity(android.content.Intent(context, AddWalletActivity::class.java))
		}

		//Refresh data on swipe refresh
		swipeRefresh.setOnRefreshListener {
			refreshData(true)
		}

		//Refresh data now, if necessary
		if(activity is MainTabbedActivity)
			if((activity as MainTabbedActivity).shouldRefresh())
				refreshData()
	}

	//Inflate view and stuff
	override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
		setHasOptionsMenu(true)
		return inflater?.inflate(R.layout.fragment_wallets, container, false)
	}

	//Set options menu
	override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: android.view.MenuInflater) {
		inflater.inflate(R.menu.menu_wallets, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	//Handle options item tap
	override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
		return when (item.itemId) {
			R.id.refresh -> {
				refreshData()
				return true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	fun refreshData(override: Boolean = false){
		//If we're already refreshing, and !override, return
		if(swipeRefresh!!.isRefreshing && !override) return

		//Set refreshing state to true
		swipeRefresh?.isRefreshing = true

		itemsToRefresh = zyzxdev.cryptopal.wallet.WalletManager.Companion.wallets.size

		//If there's nothing to refresh, call refreshedItem
		if(itemsToRefresh == 0)
			refreshedItem()

		//Refresh all wallet balances
		for(wallet in zyzxdev.cryptopal.wallet.WalletManager.Companion.wallets){
			wallet.refreshBalance(context, object: TaskCompletedCallback {
				override fun taskCompleted(data: Any?) {
					refreshedItem()
				}
			})
		}
	}

	//Counts down itemsToRefresh; if itemsToRefresh == 0 then we're done refreshing
	private fun refreshedItem(){
		if(mainListView == null) return //We exited the fragment before this loaded
		itemsToRefresh--
		if(itemsToRefresh <= 0){
			swipeRefresh?.isRefreshing = false
			(mainListView.adapter as zyzxdev.cryptopal.wallet.Wallet.WalletAdapter).notifyDataSetChanged()
		}
	}

	//Refresh ListView on resume
	override fun onResume() {
		super.onResume()
		(mainListView.adapter as zyzxdev.cryptopal.wallet.Wallet.WalletAdapter).notifyDataSetChanged()
	}


}
