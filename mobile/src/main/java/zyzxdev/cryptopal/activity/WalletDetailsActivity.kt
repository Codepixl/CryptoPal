package zyzxdev.cryptopal.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_wallet_details.*
import zyzxdev.cryptopal.dialog.QRDialog
import zyzxdev.cryptopal.R
import zyzxdev.cryptopal.util.TaskCompletedCallback
import zyzxdev.cryptopal.util.Util
import zyzxdev.cryptopal.wallet.Transaction
import zyzxdev.cryptopal.wallet.Wallet
import zyzxdev.cryptopal.wallet.WalletManager
import java.text.DecimalFormat
import java.text.NumberFormat

class WalletDetailsActivity : AppCompatActivity() {

	var wallet: Wallet? = null
	var listView: ListView? = null
	var swipeRefresh : SwipeRefreshLayout? = null

	companion object{
		val balanceFormat = DecimalFormat("#")
		init{
			balanceFormat.maximumFractionDigits = 8
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_wallet_details)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		wallet = WalletManager.wallets[intent.getIntExtra("wallet", 0)]
		supportActionBar?.title = wallet?.name

		(findViewById<TextView>(R.id.walletBalance) as TextView).text = getString(R.string.BTC_balance, balanceFormat.format(wallet!!.balance))

		val btcValue = getSharedPreferences("data", Context.MODE_PRIVATE).getFloat("btcValue", -1f).toDouble()
		val formatter = NumberFormat.getCurrencyInstance()
		if(btcValue != -1.0)
			(findViewById<TextView>(R.id.walletBalanceUSD) as TextView).text = formatter.format(wallet!!.balance*btcValue)
		else
			(findViewById<TextView>(R.id.walletBalanceUSD) as TextView).text = getString(R.string.blank_usd)

		findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
			QRDialog(this, wallet!!.address).show()
		}

		(findViewById<TextView>(R.id.lastUpdated) as TextView).text = getString(R.string.transactions_last_updated, Util.getTimeAgo(wallet!!.lastUpdated, this))

		walletAddress.text = wallet?.address

		listView = findViewById<ListView>(R.id.mainListView)
		listView?.adapter = Transaction.TransactionAdapter(this, wallet!!)


		swipeRefresh = listView?.parent as SwipeRefreshLayout //findviewbyid isn't working for some reason
		swipeRefresh?.setOnRefreshListener {
			updateTransactions(true)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_wallet_details, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when(item?.itemId){
			android.R.id.home -> {
				onBackPressed()
				return true
			}
			R.id.menu_item_delete -> {
				WalletManager.wallets.remove(wallet)
				WalletManager.save()
				finish()
				return true
			}
			R.id.menu_item_refresh -> {
				updateTransactions()
				return true
			}
			R.id.menu_item_open_in_browser -> {
				startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://blockchain.info/address/${wallet?.address}")))
				return true
			}
			else -> {
				return super.onOptionsItemSelected(item)
			}
		}
	}

	fun updateTransactions(override: Boolean = false){
		if(swipeRefresh!!.isRefreshing && !override) return
		swipeRefresh?.isRefreshing = true
		val ctx = this
		wallet?.refreshTransactions(this, object: TaskCompletedCallback {
			override fun taskCompleted(data: Object) {
				swipeRefresh?.isRefreshing = false
				(listView?.adapter as Transaction.TransactionAdapter).notifyDataSetChanged()
				(findViewById<TextView>(R.id.lastUpdated) as TextView).text = getString(R.string.transactions_last_updated, Util.getTimeAgo(wallet!!.lastUpdated, ctx))
				(findViewById<TextView>(R.id.walletBalance) as TextView).text = getString(R.string.BTC_balance, balanceFormat.format(wallet!!.balance))
			}
		})
	}
}
