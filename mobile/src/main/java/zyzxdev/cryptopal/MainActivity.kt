package zyzxdev.cryptopal

import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ListView
import android.net.ConnectivityManager
import android.opengl.Visibility
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

class MainActivity : AppCompatActivity() {

	private var listView: ListView? = null
	private var spinner: ProgressBar? = null
	private var isRefreshing = false
	private var itemsToRefresh = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_main)
		setSupportActionBar(toolbar)

		listView = findViewById(R.id.mainListView) as ListView
		listView?.adapter = Wallet.WalletAdapter(this)

		WalletHandler.init(this)

		spinner = findViewById(R.id.loadingSpinner) as ProgressBar

		fab.setOnClickListener { _ ->
			startActivity(Intent(this, AddWalletActivity::class.java))
		}

		refreshData()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
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

	private fun refreshData(){
		if(isRefreshing) return
		val ctx = this
		isRefreshing = true
		spinner?.visibility = View.VISIBLE
		itemsToRefresh = WalletHandler.wallets.size+1
		getSharedPreferences("data", Context.MODE_PRIVATE).edit().putFloat("btcValue", -1f).apply()
		DownloadTask(this).setCallback(object: TaskCompletedCallback{
			override fun taskCompleted(data: Object) {
				refreshedItem()
				try {
					val btc = (data as String).toFloat()
					getSharedPreferences("data", Context.MODE_PRIVATE).edit().putFloat("btcValue", btc).apply()
				}catch(e: NumberFormatException){
					Toast.makeText(ctx, R.string.error_updating, Toast.LENGTH_SHORT).show()
				}
			}
		}).execute("https://blockchain.info/q/24hrprice")

		for(wallet in WalletHandler.wallets){
			wallet.refreshBalance(ctx, object: TaskCompletedCallback{
				override fun taskCompleted(data: Object) {
					refreshedItem()
				}
			})
		}
	}

	private fun refreshedItem(){
		itemsToRefresh--
		if(itemsToRefresh == 0){
			isRefreshing = false
			spinner?.visibility = View.GONE
			(listView?.adapter as Wallet.WalletAdapter).notifyDataSetChanged()
		}
	}

	override fun onResume() {
		super.onResume()
		(listView?.adapter as Wallet.WalletAdapter).notifyDataSetChanged()
	}


}
